package netty.pro.protocol;

public enum MessageType {

    REQ((byte) 0), //请求
    RESP((byte) 1), //应答
    ONE_WAY((byte) 2), //既是请求也是应答
    HEARTBEAT_REQ((byte) 3), //心跳请求
    HEARTBEAT_RESP((byte) 4),//心跳应答
    RPC_REP((byte) 5),//心跳应答
    RPC_RESP((byte) 6);//心跳应答

    private byte value;

    private MessageType(byte value) {
        this.value = value;
    }
    public byte value() {
        return this.value;
    }
}


