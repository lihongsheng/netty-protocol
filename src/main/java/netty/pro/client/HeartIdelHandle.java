package netty.pro.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import netty.pro.protocol.MessageType;
import netty.pro.protocol.Protocol;

public class HeartIdelHandle  extends IdleStateHandler{

    private Client client;
    public HeartIdelHandle(Client client,int readOutTime, int writeOutTime){

        super(readOutTime,writeOutTime,10);
        this.client = client;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx,msg);
//        Protocol message = (Protocol) msg;
//        // 返回心跳应答消息
//        if (message.getType() == MessageType.HEARTBEAT_REQ.value()) {
//            ctx.writeAndFlush(Protocol.buildHeart(MessageType.HEARTBEAT_RESP.value(),message.getRequestId()));
//            System.out.println("来自[[[服务端]]]的心跳[[[[请求包]]]]::" + message.getRequestId());
//        } else if (message.getType() == MessageType.HEARTBEAT_RESP.value()){//服务端发来的心跳应答包，不用理会
//            System.out.println("来自[[[服务端]]]的心跳[[[[应答包]]]]::" + message.getRequestId());
//            //ctx.writeAndFlush(Protocol.buildHeart(MessageType.HEARTBEAT_REQ.value(),System.currentTimeMillis()));
//        } else {
//            //传递给下一个handle
//            System.out.println(String.format("来自服务端的未知消息类型::%d" , (int)message.getType()));
//            ctx.fireChannelRead(msg);
//           // super.channelRead(ctx,msg);
//        }
    }

    /**
     *
     * @param ctx
     */
    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {

        //规定时间内既没有读也没写事件，发送心跳事件
        if (IdleStateEvent.ALL_IDLE_STATE_EVENT == evt) {
            System.out.println("发送心跳请求包::");
            Protocol protocol = Protocol.buildHeart(MessageType.HEARTBEAT_REQ.value(),System.currentTimeMillis());
            ctx.channel().writeAndFlush(protocol);
        }
        //第一个心跳包长时间没接受到，断开重连
        if (IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT == evt || IdleStateEvent.READER_IDLE_STATE_EVENT == evt ) {
            //关闭链接
            System.out.println("服务端超时关闭链接::" + ctx.channel().id().asLongText());
            ctx.channel().close();
           //重连服务
           client.connect();;
        }
        super.channelIdle( ctx,  evt);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        //tcp断开后重连
        System.out.println("TCP断开重连::" + ctx.channel().id().asLongText());
        client.connect();
        super.channelInactive(ctx);
    }
}
