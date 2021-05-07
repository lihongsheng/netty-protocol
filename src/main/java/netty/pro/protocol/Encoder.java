package netty.pro.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

/**
 *
 */
public class Encoder extends MessageToByteEncoder<Protocol>  {

    public Encoder() {}

    @Override
    protected void encode(ChannelHandlerContext ctx, Protocol msg, ByteBuf sendBuf) throws Exception {
        if (msg == null || msg.getRequestId() < 0)
            throw new Exception("The Encoder message is null");

        sendBuf.writeInt(msg.getVersion());
        //写入报文长度，非真实长度，先占位
        sendBuf.writeInt(msg.getLength());
        sendBuf.writeLong(msg.getRequestId());
        sendBuf.writeByte(msg.getType());


        System.out.println("消息类型" + msg.getType());

        try {
            if (msg.getBody() != null) {
                sendBuf.writeBytes(msg.serializeToByte());
            }
        }catch (Exception e) {
            System.out.println("异常" + e.getMessage());
        }

        System.out.println("消息类型--" + (sendBuf.readableBytes() - Protocol.HEAD_SIZE - Protocol.PROTOCOL_LENGTH));
        //更新真实报文长度到占位符
        sendBuf.setInt(4, sendBuf.readableBytes() - Protocol.HEAD_SIZE - Protocol.PROTOCOL_LENGTH);
        //编码完成
    }
}
