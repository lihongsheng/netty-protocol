package netty.pro.client;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import netty.pro.ServerApi.ServerMap;
import netty.pro.common.CustomLoadClass;
import netty.pro.common.Message;
import netty.pro.common.Response;
import netty.pro.protocol.MessageType;
import netty.pro.protocol.Protocol;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import io.netty.channel.Channel;
public class RpcHandle  extends ChannelHandlerAdapter{
    CustomLoadClass customLoadClass;
    private ConcurrentHashMap<Long,SynchronousQueue<Object>> requestMap = new ConcurrentHashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Protocol protocol = (Protocol) msg;

        if (protocol.getType() == MessageType.RPC_REP.value()) {
            System.out.println("接受到来自 客户端的RCP请求！！！！！");
            //根据调用类找到并加装类，然后调用实体类.
            Message message = (Message) protocol.getBody();
            //调用实体类
          //  Class<?> cls = customLoadClass.loadClass(message.getClassName());

            Class<?> cls = ServerMap.get(message.getClassName()).getClass();
            System.out.println(message.getMethodName());
            System.out.println(message.getParamType().toString());
            Method method = cls.getMethod(message.getMethodName(),message.getParamType());

            Object returnData = method.invoke(ServerMap.get(message.getClassName()),message.getParams());
            Class<?> returnType = method.getReturnType();

            //编辑此次应答报文实体
            Response response = new Response();
            response.setData(returnData);
            response.setReturnType(returnType);
            //返回此次应答报文
            protocol.setBody(response);
            protocol.setType(MessageType.RPC_RESP.value());
            message = null;
            ctx.writeAndFlush(protocol);

        } else if (protocol.getType() == MessageType.RPC_RESP.value()) {//RPC 应答包
            System.out.println("接受到RPC应答报文");
            //抛出应答包
            Response response = (Response)protocol.getBody();
            Long requestId = protocol.getRequestId();
            SynchronousQueue<Object> queue = requestMap.get(requestId);
            queue.put(response);
            requestMap.remove(requestId);
            //ctx.writeAndFlush(Protocol.buildHeart(MessageType.HEARTBEAT_REQ.value(),System.currentTimeMillis()));
        }

    }

    /**
     * 发送协议报文
     * @param protocol
     * @param channel
     * @return
     */
    public SynchronousQueue<Object> send(Protocol protocol,Channel channel)
    {
        SynchronousQueue<Object> queue = new SynchronousQueue<>();
        requestMap.put(protocol.getRequestId(),queue);
        channel.writeAndFlush(protocol);
        System.out.println("！！！发送RPC报文    完毕！！！");
        return queue;
    }


}
