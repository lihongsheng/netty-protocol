package netty.pro.common;

import netty.pro.common.ObjectSerialize;

import java.io.Serializable;

public class Message implements Serializable {


    private String className;
    private String methodName;
    private Class<?>[] paramType;
    private Object[] params;

    public Class<?>[] getParamType() {
        return paramType;
    }

    public void setParamType(Class<?>[] paramType) {
        this.paramType = paramType;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }



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
}
