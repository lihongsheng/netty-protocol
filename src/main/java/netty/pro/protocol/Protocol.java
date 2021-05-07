package netty.pro.protocol;

import netty.pro.common.Message;
import netty.pro.common.ObjectSerialize;

/**
 * 协议接受
 * 前四个字节表示 协议头，如协议版本号,包体的协议类型
 * 5-8，四个字节表示整个协议包的长度
 * 后8个字节表示请求ID
 * 后一个字节表示 请求类型
 * 在后边是报文
 */

public class Protocol {
    //协议头
    public final static int HEAD_SIZE = 4;
    //协议包的所在长度值为 [ 请求ID的8个字节+请求type的一个字节 + body的字节数]
    public final static int PROTOCOL_LENGTH = 4;
    //请求ID的长度
    public final static int REQUESID_LENGTH = 8;
    //类型的长度
    public final static int TYPE_LENGTH = 1;


    public static ObjectSerialize serialize;

    private int version = 0xabef0101;
    //
    private int length = 0;// 消息长度
    //每次请求ID 64为 8个字节
    private long requestId;
    //消息类 8位一个字节
    private byte type;
    //
    private Object body;

    public static  void setSerialize(ObjectSerialize ser) {
        serialize = ser;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    /**
     * 序列化转为二进制
     *
     * @return
     */
    public byte[] serializeToByte() {
        if (body == null) {
            return null;
        }
        try {
            //byte[] bytes = serialize.toJson(body).getBytes("UTF-8");
            byte[] bytes = serialize.objectToByte(body);
            return bytes;
        } catch (Exception e) {
            System.out.println("异常##" + e.getMessage());
            return null;
        }


    }

    /**
     * 反序列化
     *
     * @param bytes
     */
    public void ByteToObject(byte[] bytes,Class<?> cls) {

       // body = serialize.fromJson(bytes, Message.class);
       body = serialize.ByteToObject(bytes, cls);
    }

    public String toString() {
        return String.format("version:%d;requestId:%d", version, requestId);
    }


    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }


    /**
     * 构建心跳包
     *
     * @param type
     * @param requestId
     * @return
     */
    public static Protocol buildHeart(byte type, long requestId) {
        Protocol pro = new Protocol();
        pro.setRequestId(requestId);
        pro.setType(type);
        pro.setBody(null);
        return pro;
    }

}
