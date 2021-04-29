package netty.pro.protocol;

import java.util.List;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 依托于ByteMessageDecode类
 *   ByteMessageDecode 的作用，在于缓存接受来自于tcp发送过来的数据【 在ByteMessageDecode.cumulation】。
 *   由于是基于TCP的报文，对于大的应用报文，系统在TCP层会对应用层的大的报文做分片，小的报文做合并一起转发。
 *  * tcp 报文例子
 *  * TCP报文1
 *  * -------------------------------------------------
 *  * |tcp报文头部|应用报文头部|应用报文内容的一半或者一小部分|
 *  * -------------------------------------------------
 *  * TCP报文2或者第N个
 *  * --------------------------------------------------------------------------
 *  *|tcp报文头部|应用报文内容的一半或者一小部分|应用报文头部|应用报文内容的一半或者一小部分
 *  * ---------------------------------------------------------------------------
 *  我们需要的是设计自己的接码，并重写decode接口，从ByteMessageDecode.cumulation读取数据并判断是否足够接码的数据，如果数据足够编码一个数据报文给下一个handle。
 *  把解析好的报文放到decode的【 List<Object> out】参数里，ByteMessageDecode类会判断out是否有值，有值的话会传递给下一个handle。
 *  为了防止数据错误导致的内存溢出等问题，依据LengthFieldBasedFrameDecoder来编写解码即可。
 */
public class CustomDecoder extends  ByteToMessageDecoder{
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < (Protocol.HEAD_SIZE + Protocol.PROTOCOL_LENGTH)) {
            return;
        }
        int originIndex = in.readerIndex();

        int version = in.readInt();
        int bodyLength = in.readInt();

        if (in.readableBytes() < bodyLength) {
            in.readerIndex(originIndex);
            return;
        }

        Protocol protocol = new Protocol();
        protocol.setVersion(version);
        protocol.setLength(bodyLength);

        protocol.setRequestId(in.readLong());
        protocol.setType(in.readByte());

        int dataLength = bodyLength - Protocol.REQUESID_LENGTH - Protocol.TYPE_LENGTH;
        if (dataLength> 0) {
            byte[] bytes = new byte[dataLength];
            in.readBytes(bytes);
            protocol.ByteToObject(bytes);
        }
        out.add(protocol);
    }
}
