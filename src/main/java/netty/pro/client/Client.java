package netty.pro.client;

import com.google.gson.Gson;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import netty.pro.ServerApi.User;
import netty.pro.common.JsonSerialize;
import netty.pro.common.Message;
import netty.pro.common.ProxyFactory;
import netty.pro.entiry.UserInfo;
import netty.pro.protocol.*;

import java.util.concurrent.TimeUnit;

public class Client {
    private NioEventLoopGroup nioEventLoopGroup;
    private Bootstrap serverBootstrap;
    private Channel channel;

    private int port;
    protected String host;
    RpcHandle rpcHandle;

    public Client(String host,int port) {
        this.host = host;
        this.port = port;
        rpcHandle = new RpcHandle();
    }
    public Client() {}

    public void start() {
        nioEventLoopGroup = new NioEventLoopGroup();
        serverBootstrap   = new Bootstrap();
        serverBootstrap.group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new Decoder(1024*1024,4,4));
                      //  ch.pipeline().addLast(new CustomDecoder());
                        ch.pipeline().addLast(new Encoder());
                        ch.pipeline().addLast(new HeartIdelHandle(Client.this,60,60));
                        ch.pipeline().addLast(new MessageHandle());
                        ch.pipeline().addLast(rpcHandle);
                    }
                });
        connect();
    }

    public void connect() {
        System.out.println("Connect........................!!!");
        if (channel != null && channel.isActive()) {
            return;
        }

        ChannelFuture future = serverBootstrap.connect(host, port);
        //监听断开连接后重连
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture futureListener) throws Exception {
                if (futureListener.isSuccess()) {
                    channel = futureListener.channel();
                    System.out.println(String.format("Connect to server successfully! %s",channel.id()));
                    sendTestData();
                    //sendRPCData();
                } else {
                    System.out.println("Failed to connect to server, try connect after 10s");
                    futureListener.channel().eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
                            connect();
                        }
                    }, 10, TimeUnit.SECONDS);
                }
            }
        });
    }


    public void sendTestData()
    {
        if (channel != null && channel.isActive()) {
            Protocol pro = new Protocol();
            pro.setRequestId(System.currentTimeMillis());
            pro.setLength(999999999);
            pro.setType(MessageType.REQ.value());
            Message message = new Message();
            message.setClassName(String.format("CLASSS%d", 10));
            message.setMethodName(String.format("METHOD%d", 10));
            pro.setBody(message);
            channel.writeAndFlush(pro);
        } else {
            System.out.println("CLIENT发送报文失败");
        }


    }

    public void sendRPCData()
        {
            if (channel != null && channel.isActive()) {
                System.out.println("clinet rpc");
                User user =(User) ProxyFactory.createObject(User.class, rpcHandle, channel);
               UserInfo userInfo =  user.get(123456);
               System.out.println("用户姓名::" + userInfo.getName());
            } else {
                System.out.println("CLIENT发送报文失败");
            }
        }

    public static void main(String[] args)  {
       // Client client = new Client(args[1],Integer.parseInt(args[2]));
        //设置序列的协议类型
        Protocol.setSerialize(new JsonSerialize());
        Client client = new Client("127.0.0.1",12701);
        client.start();

        //Thread.sleep(5000);
        //client.sendTestData();
        new Thread(new Runnable() {
            @Override
            public void run()  {
                try {
                    System.out.println("过6秒后，发送一个RPC报文");
                    Thread.sleep(6000);
                    client.sendRPCData();
                }catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }
        }).start();



    }
}
