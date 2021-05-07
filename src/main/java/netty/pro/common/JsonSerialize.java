package netty.pro.common;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;

public class JsonSerialize implements ObjectSerialize {
    public byte[] objectToByte(Object body) {
        try {
            //System.out.println("异常！！" + (new Gson().newBuilder().excludeFieldsWithoutExposeAnnotation().create()).toJson(body));
           // byte[] bytes = (new Gson().newBuilder().excludeFieldsWithoutExposeAnnotation().create()).toJson(body).getBytes("UTF-8");
            byte[] bytes = JSON.toJSONBytes(body);
            return bytes;
        } catch (Exception e) {
            System.out.println("异常！！" + e.getMessage());
            return null;
        }
    }

    public Object ByteToObject(byte[] bytes,Class<?> cla) {
       // T body =  (new Gson()).fromJson(new String(bytes), cla);
        Object body =  JSON.parseObject(bytes,cla);
        return body;
    }
}
