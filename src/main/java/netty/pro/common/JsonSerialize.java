package netty.pro.common;

import com.google.gson.Gson;
import netty.pro.protocol.Message;

public class JsonSerialize implements ObjectSerialize {
    public byte[] objectToByte(Object body) {
        try {
            byte[] bytes = (new Gson()).toJson(body).getBytes("UTF-8");
            return bytes;
        } catch (Exception e) {
            return null;
        }
    }

    public <T> T ByteToObject(byte[] bytes,Class<T> cla) {
        T body =  (new Gson()).fromJson(new String(bytes), cla);
        return body;
    }
}
