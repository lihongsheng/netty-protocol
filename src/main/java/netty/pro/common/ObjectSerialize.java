package netty.pro.common;

public interface ObjectSerialize {
    public byte[] objectToByte(Object object);
    public <T> T ByteToObject(byte[] bytes,Class<T> cla);
}
