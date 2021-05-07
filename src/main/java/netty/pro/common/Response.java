package netty.pro.common;

import java.io.Serializable;

public class Response implements Serializable {

    private Class<?> returnType;
    private Object data;

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
