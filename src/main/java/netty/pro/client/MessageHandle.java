package netty.pro.client;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import netty.pro.common.Message;
import netty.pro.protocol.MessageType;
import netty.pro.protocol.Protocol;

import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import io.netty.channel.Channel;
public class MessageHandle extends  ChannelHandlerAdapter{


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Protocol protocol = (Protocol) msg;

        if (protocol.getType() == MessageType.HEARTBEAT_REQ.value()) {
            ctx.writeAndFlush(Protocol.buildHeart(MessageType.HEARTBEAT_RESP.value(),protocol.getRequestId()));
            System.out.println("来自[[[服务端]]]的心跳[[[[请求包]]]]::" + protocol.getRequestId() + ";;;");
            return;
        } else if (protocol.getType() == MessageType.HEARTBEAT_RESP.value()){//服务端发来的心跳应答包，不用理会
            System.out.println("来自[[[服务端]]]的心跳[[[[应答包]]]]::" + protocol.getRequestId());
            ctx.writeAndFlush(Protocol.buildHeart(MessageType.HEARTBEAT_REQ.value(),System.currentTimeMillis()));
            return;
        }

        // 请求报文
        if (protocol.getType() == MessageType.REQ.value()) {
           Message message =(Message) protocol.getBody();
            System.out.println("客户端-----1----::" + "CLIENT::" + message.getClassName());
            message.setClassName("CLIENT::" +message.getClassName());
            message.setMethodName("CLIENT::" +message.getMethodName());
            protocol.setType(MessageType.RESP.value());
            ctx.channel().writeAndFlush(protocol);
        } else if (protocol.getType() == MessageType.RESP.value()) {//应答报文
            Message message =(Message) protocol.getBody();
            System.out.println("客户端-----2----::"+ "CLIENT::" + message.getClassName());
//            protocol.getBody().setClassName("CLIENT::" +protocol.getBody().getClassName());
//            protocol.getBody().setMethodName("CLIENT::" +protocol.getBody().getMethodName());
//            protocol.setRequestId(System.currentTimeMillis());
//            ctx.writeAndFlush(protocol);
            //传递给下一个handle
            ctx.fireChannelRead(msg);
        } else {
            System.out.println("来自服务端---------的未知消息类型::" + protocol.getRequestId());
         //   Message message =(Message) protocol.getBody();
           // System.out.println("来自服务端---------的未知消息类型::" + message.getClassName());
            ctx.fireChannelRead(msg);
        }
    }

}
