package netty.pro.common;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.netty.channel.*;
import netty.pro.client.RpcHandle;
import netty.pro.protocol.MessageType;
import netty.pro.protocol.Protocol;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;

public class ProxyFactory  {

    public  static Object createObject( Class<?> cls,RpcHandle rpcHandle,Channel channel)  throws IllegalArgumentException {
        //如果是接口类并且是service注解添加到serviceMap等 【未做】
       return Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{cls},  new DynamicProxy(rpcHandle,channel));
    }


}
