package netty.pro.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import netty.pro.common.Message;
import netty.pro.common.Response;

import java.io.IOException;

/**
 * 由于是基于TCP的报文，对于大的应用报文，系统在TCP层会对应用层的大的报文做分片，小的报文做合并一起转发。
 * tcp 报文例子
 * TCP报文1
 * -------------------------------------------------
 * |tcp报文头部|应用报文头部|应用报文内容的一半或者一小部分|
 * -------------------------------------------------
 * TCP报文2或者第N个
 * --------------------------------------------------------------------------
 *|tcp报文头部|应用报文内容的一半或者一小部分|应用报文头部|应用报文内容的一半或者一小部分
 * ---------------------------------------------------------------------------
 * 需要我们自己设计的协议来标识应用层的报文格式报文长度。
 * 以便netty来处理，这里基于LengthFieldBasedFrameDecoder类，我们只需定义好报文长度字节开始位置，报文长度字节结束位置。由LengthFieldBasedFrameDecoder
 * 类来从byteBuf读取一个完整的报文，然后上报个应用层使用。当然我们也可以基于ByteToMessageDecoder来实现一个自己的由LengthFieldBasedFrameDecoder。
 * byteBuf是一个用来接收tcp报文的数据缓存类，所有的某一个channel的TCP数据报文的都按照tcp报文顺序插入到byteBuf。
 * 因此，我们依据我们的报文格式来顺序读取数据，就能从byteBuf读取一个完整报文。
 */
public class Decoder extends LengthFieldBasedFrameDecoder {

    /**
     * @param maxFrameLength    最大允许的报文长度
     * @param lengthFieldOffset 标识数据报文长度字节开始位置
     * @param lengthFieldLength 标识数据报文长度字节结束位置
     * @throws IOException
     */
    public Decoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) throws IOException {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    /**
     *
     * @param ctx channel上下文
     * @param in tcp收到数据缓存类
     * @return
     * @throws Exception
     */
    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }

        Protocol pro = new Protocol();
        //协议版本
        pro.setVersion(frame.readInt());
        //报文总长度，整条协议【协议版本。。】
        pro.setLength(frame.readInt());
        //请求ID
        pro.setRequestId(frame.readLong());
        //请求类型
        pro.setType(frame.readByte());
        //获取包体的长度
        int bodyLength = pro.getLength()  - Protocol.REQUESID_LENGTH - Protocol.TYPE_LENGTH;

        //只有报文头部，包体无报文
        if (bodyLength == 0) {
            pro.setBody(null);
            return pro;
        }
        //获取报文
        byte[] bytes = new byte[bodyLength];
        frame.readBytes(bytes);


        System.out.println(" !!!----000---!!消息类型" + pro.getType());
        //增加根据请求类型实例化不同的类
        if (pro.getType() == MessageType.HEARTBEAT_REQ.value() ||
                pro.getType() == MessageType.HEARTBEAT_RESP.value() ||
                pro.getType() == MessageType.REQ.value() ||
                pro.getType() == MessageType.RPC_REP.value() ||
                pro.getType() == MessageType.RESP.value()) {//应答报文


            pro.ByteToObject(bytes, Message.class);
        }

        //RPC应答包
        if (pro.getType() == MessageType.RPC_RESP.value() ) {
            System.out.println(" !!!---!!!!!----!!消息类型" + pro.getType());
            pro.ByteToObject(bytes, Response.class);
        }


        return pro;
    }

}
