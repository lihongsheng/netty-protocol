package netty.pro.protocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class Test {

    public Object decode( ByteBuf frame) {

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
        int bodyLength = pro.getLength() - Protocol.REQUESID_LENGTH - Protocol.TYPE_LENGTH;
        //只有报文头部，包体无报文
        if (bodyLength <= 0) {
            pro.setBody(null);
            return pro;
        }
        byte[] bytes = new byte[bodyLength];
        frame.readBytes(bytes);
        pro.ByteToObject(bytes);
        return pro;
    }

    public void encode(Protocol msg, ByteBuf sendBuf) throws Exception {
        if (msg == null || msg.getRequestId() < 0)
            throw new Exception("The Encoder message is null");

        sendBuf.writeInt(msg.getVersion());
        //写入报文长度，非真实长度，先占位
        sendBuf.writeInt(msg.getLength());
        sendBuf.writeLong(msg.getRequestId());
        sendBuf.writeByte(msg.getType());
        if (msg.getBody() != null) {
            sendBuf.writeBytes(msg.serializeToByte());
        }
        //更新真实报文长度到占位符
        System.out.println(String.format("ENCODE::%d",sendBuf.readableBytes()));
        sendBuf.setInt(4, sendBuf.readableBytes() - Protocol.HEAD_SIZE - Protocol.PROTOCOL_LENGTH);
        //编码完成
    }

    public Protocol createMsg(int index, Message message)
    {
        Protocol pro = new Protocol();
        pro.setRequestId(999999999 + index);
        pro.setLength(999999999);
        pro.setType(MessageType.REQ.value());
        pro.setBody(message);
        return pro;
    }

    public static void main(String[] args) throws Exception {
        Test test = new Test();

        Message message;
        for (int i = 0; i < 5; i++) {
            ByteBuf sendBuf = Unpooled.buffer();
            if ((i%2) == 0) {
                message = null;
            } else {
                message = new Message();
                message.setClassName(String.format("CLASSS%d",i));
                message.setMethodName(String.format("METHOD%d",i));
            }
            Protocol pro = test.createMsg(i,message);
            test.encode(pro,sendBuf);


            Protocol pro2 = (Protocol) test.decode(sendBuf);
            System.out.println(pro2.getRequestId());
            if ((i%2) != 0) {
                System.out.println(pro2.getBody().getClassName());
            }
        }

//        for (int i = 0; i < 5; i++) {
//
//        }
    }
}
