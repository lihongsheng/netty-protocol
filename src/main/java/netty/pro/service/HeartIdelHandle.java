package netty.pro.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

public class HeartIdelHandle  extends IdleStateHandler{

    public HeartIdelHandle(int readOutTime, int writeOutTime){
        super(readOutTime,writeOutTime,10);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx,msg);
//        Protocol message = (Protocol) msg;
//        // 返回心跳应答消息
//        if (message.getType() == MessageType.HEARTBEAT_REQ.value()) {
//            ctx.writeAndFlush(Protocol.buildHeart(MessageType.HEARTBEAT_RESP.value(),message.getRequestId()));
//            System.out.println("来自客户端的心跳[[[[请求包]]]]::" + message.getRequestId());
//        } else if (message.getType() == MessageType.HEARTBEAT_RESP.value()) {//客户端发来的心跳应答包，不用理会
//            System.out.println("来自客户端的心跳[[[应答包]]]::" + message.getRequestId());
//            //ctx.writeAndFlush(Protocol.buildHeart(MessageType.HEARTBEAT_REQ.value(),System.currentTimeMillis()));
//        } else {
//            //传递给下一个handle
//            System.out.println(String.format("来自客户端的未知消息类型%d" , (int)message.getType()));
//            //传递给下一个handle
//           // ctx.fireChannelRead(msg);
//            super.channelRead(ctx,msg);
//        }
    }

    /**
     *
     * @param ctx
     */
    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {

        if (IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT == evt || IdleStateEvent.READER_IDLE_STATE_EVENT == evt) {
            //关闭链接
            System.out.println("客户端超时关闭链接::"+ctx.channel().id().asLongText());
            ctx.channel().close();
        }
        super.channelIdle( ctx,  evt);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        //tcp断开后重连
        System.out.println("TCP断开重连::" + ctx.channel().id().asLongText());
    }

}