package netty.pro.common;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
public interface HeartCustom {
   public void handleIdel(ChannelHandlerContext ctx, IdleStateEvent evt);
   public void channelRead(ChannelHandlerContext ctx, Object msg);
}
