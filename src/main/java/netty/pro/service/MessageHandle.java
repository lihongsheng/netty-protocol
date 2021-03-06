package netty.pro.service;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import netty.pro.common.Message;
import netty.pro.protocol.MessageType;
import netty.pro.protocol.Protocol;

public class MessageHandle extends  ChannelHandlerAdapter{

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Protocol protocol = (Protocol) msg;

        System.out.println("来自[[[服务端]]]的心跳[[[[请求包]]]]::" + protocol.getType());

        if (protocol.getType() == MessageType.HEARTBEAT_REQ.value()) {
            ctx.writeAndFlush(Protocol.buildHeart(MessageType.HEARTBEAT_RESP.value(),protocol.getRequestId()));
            System.out.println("来自客户端的心跳[[[[请求包]]]]::" + protocol.getRequestId());
            return;
        } else if (protocol.getType() == MessageType.HEARTBEAT_RESP.value()) {//客户端发来的心跳应答包，不用理会
            System.out.println("来自客户端的心跳[[[应答包]]]::" + protocol.getRequestId());
            return;
            //ctx.writeAndFlush(Protocol.buildHeart(MessageType.HEARTBEAT_REQ.value(),System.currentTimeMillis()));
        }

        // 请求报文
        if (protocol.getType() == MessageType.REQ.value()) {
            Message message =(Message) protocol.getBody();
            System.out.println("服务端----1-----::" + "SERVER::" +message.getClassName());
            //应答报文
            message.setClassName("SERVER::" +message.getClassName());
            message.setMethodName("SERVER::" +message.getMethodName());
            protocol.setType(MessageType.RESP.value());
            ctx.channel().writeAndFlush(protocol);
            return;
        } else if (protocol.getType() == MessageType.RESP.value()) {//应答报文
            System.out.println("服务端----2-----::");
            //传递给下一个handle
//            protocol.getBody().setClassName("SERVER::" +protocol.getBody().getClassName());
//            protocol.getBody().setMethodName("SERVER::" +protocol.getBody().getMethodName());
//            protocol.setRequestId(System.currentTimeMillis());
//            ctx.writeAndFlush(protocol);

            return;
        } else {
            System.out.println("来自客户端------------的未知消息类型::" + protocol.getRequestId());
            Message message =(Message) protocol.getBody();
            System.out.println("来自客户端------------的未知消息类型::" + message.getClassName());
            ctx.fireChannelRead(msg);
        }
    }

}
