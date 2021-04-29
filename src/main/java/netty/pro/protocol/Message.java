package netty.pro.protocol;

import java.io.Serializable;

public class Message implements Serializable {

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    private String className;
    private String methodName;
}
