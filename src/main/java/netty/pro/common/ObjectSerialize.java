package netty.pro.common;

public interface ObjectSerialize {
    public byte[] objectToByte(Object object);
    public Object ByteToObject(byte[] bytes,Class<?> cla);
}
