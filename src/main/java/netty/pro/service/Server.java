package netty.pro.service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import netty.pro.protocol.Decoder;
import netty.pro.protocol.Encoder;

/**
 * @author xiongyongshun
 * @email yongshun1228@gmail.com
 * @version 1.0
 * @created 16/9/18 12:59
 */
public class Server {
    //启动服务
    public static void main(String[] args) throws Exception {
        System.out.println(String.format("启动成功:%d",12701));
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workGroup = new NioEventLoopGroup(4);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap
                    .group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                           // ChannelPipeline ch = socketChannel;
                            ch.pipeline().addLast(new Decoder(1024*1024,4,4));
                            ch.pipeline().addLast(new Encoder());
                            ch.pipeline().addLast(new HeartIdelHandle(60, 60));
                            ch.pipeline().addLast(new MessageHandle());
                        }
                    });

            //Channel ch = bootstrap.bind(Integer.parseInt(args[1])).sync().channel();
            Channel ch = bootstrap.bind(12701).sync().channel();
            System.out.println(String.format("启动成功:%d",12701));
            ch.closeFuture().sync();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}