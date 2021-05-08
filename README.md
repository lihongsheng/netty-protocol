# netty-protocol
netty 编写自定义协议例子

#### 为什么做这个？

项目中遇到一个需要通过服务端来操作设备端执行一个命令的需求，需要设备长链到服务端。所以需要一套用来设备端和服务端通信的协议，且自定义比较强些。

#### 需要关注的点

* 自动重连及定时上报心跳请求，便于服务端判断设备是否存活。

  心跳主要的作用是探活，用来看看客户端和服务端的链接是否断开。以便客户端做断线重连，服务端用来清理断开的客户端数据。

* 协议自定义比较高且简单，便于后边人员维护。业务之间的报文可自行选择序列化协议,如json,protobuf

  自己定义了一个协议，但是实际的序列化和反序列化可以自己选择

#### 实现简述
+ 1:心跳的实现：

​        心跳类继承与io.netty.handler.timeout.IdleStateHandler。io.netty.handler.timeout.IdleStateHandler大致原理是基于时间在channel链接是通的时候会初始化一个并添加一个定时的Runnable类【如ReaderIdleTimeoutTask】到channel的schedule调度里去。
+ 2：定义了一个自己的协议类型
  
    ```java
    ----------------
    |前四个字节表示版本号|
    |后四个自己表示协议后边的长度 【注:请求流水号+请求类型+实际报文的长度】|
    |这8个字节是每次请求的流水号|
    |一个字节表示请求类型|
    |后边跟的是实际包文|
    --------------
    ```
    
    - 2.1：TCP报文的数据传输与接收
    
      ```
      由于是基于TCP的报文，对于大的应用报文，系统在TCP层会对应用层的大的报文做分片，小的报文做合并一起转发。
       *  * tcp 报文例子
       *  * TCP报文1
       *  * -------------------------------------------------
       *  * |tcp报文头部|应用报文头部|应用报文内容的一半或者一小部分|
       *  * -------------------------------------------------
       *  * TCP报文2或者第N个
       *  * --------------------------------------------------------------------------
       *  *|tcp报文头部|应用报文内容的一半或者一小部分|应用报文头部|应用报文内容的一半或者一小部分
       *  * ---------------------------------------------------------------------------
       *  我们需要的是设计自己的接码，并重写decode接口
      ```
    
    - 2.2为什么要编写解码类
    
      有上边可知，由于TCP发送的报文不一定是一个完整的数据报文，所以需要netty缓存每次tcp的报文数据，当数据足够一个报文的时候，我们来处理。
    
      ​          这里有个实现。一个是基于 io.netty.handler.codec.ByteToMessageDecoder来处理的类，这个需要自己判断当前读取的缓存里数据长度和根据自己的协议类型从netty的缓存数据里取出数据做处理，实现的列子是【netty.pro.protocol.CustomDecoder】。另外一个是基于 io.netty.handler.codec.LengthFieldBasedFrameDecoder，这netty已经做好截取的处理，我们依托于这个类就可以，而且这个类做了数据错乱的或者恶意数据处理，我们依托于这个就行，实现的列子是【netty.pro.protocol.Decoder】。
    
    - 2.3 编写编码类。
    
      编码类就是把我们实际要发送的东西格式为一个二进制数据包，给到TCP发送出去。我们依托于io.netty.handler.codec.MessageToByteEncoder来实现的【Encoder】
    
+ 3：定义自己处理数据报文的实际内容MessageHandle

    定义一个handle来处理实际的报文类型

实现的客户端入口为：netty.pro.client.Client; 服务端入口为：netty.pro.service.Server

#### 第二阶段【基于Json或者protobuf实现一个简单的RPC】

##### 需要关注的点

* 加载类的实现
* 序列化对象
* 反序列化对象，并且调用对象，并把返回的结果序列化返回