package netty.pro.common;

import com.google.gson.Gson;
import io.netty.channel.Channel;
import netty.pro.client.RpcHandle;
import netty.pro.protocol.MessageType;
import netty.pro.protocol.Protocol;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;

public class DynamicProxy implements InvocationHandler
{
    RpcHandle rpcHandle;
    Channel channel;
    public DynamicProxy(RpcHandle rpcHandle,Channel channel)
    {
        this.channel = channel;
        this.rpcHandle = rpcHandle;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Protocol pro = new Protocol();
        pro.setRequestId(System.currentTimeMillis());
        pro.setLength(0);
        pro.setType(MessageType.RPC_REP.value());

        Message message = new Message();
        message.setClassName(method.getDeclaringClass().getName());
        message.setMethodName(method.getName());

        System.out.println(method.getDeclaringClass().getName());
        message.setParams(args);
        message.setParamType(method.getParameterTypes());

        pro.setBody(message);

        System.out.println("！！！发送RPC报文！！！");
        SynchronousQueue<Object> queue =rpcHandle.send(pro,channel);

        Response response = (Response)queue.take();
        System.out.println("！！！应答RPC报文！！！");
        Class<?> returnType = response.getReturnType();

        if (returnType.isPrimitive() || String.class.isAssignableFrom(returnType)){
            return response.getData();
        }else if (Collection.class.isAssignableFrom(returnType)){
            return (new Gson()).fromJson(response.getData().toString(),Object.class);
        }else if(Map.class.isAssignableFrom(returnType)){
            return (new Gson()).fromJson(response.getData().toString(),Map.class);
        }else{
            Object data = response.getData();
            return (new Gson()).fromJson(data.toString(), returnType);
        }
    }
}
