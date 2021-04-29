package netty.pro.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import netty.pro.protocol.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Client {
    private NioEventLoopGroup nioEventLoopGroup;
    private Bootstrap serverBootstrap;
    private Channel channel;

    private int port;
    protected String host;

    public Client(String host,int port) {
        this.host = host;
        this.port = port;
    }
    public Client() {}

    public void start() {
        nioEventLoopGroup = new NioEventLoopGroup();
        serverBootstrap   = new Bootstrap();
        serverBootstrap.group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new Decoder(1024*1024,4,4));
                      //  ch.pipeline().addLast(new CustomDecoder());
                        ch.pipeline().addLast(new Encoder());
                        ch.pipeline().addLast(new HeartIdelHandle(Client.this,60,60));
                        ch.pipeline().addLast(new MessageHandle());
                    }
                });
        connect();
    }

    public void connect() {
        System.out.println("Connect........................!!!");
        if (channel != null && channel.isActive()) {
            return;
        }

        ChannelFuture future = serverBootstrap.connect(host, port);
        //监听断开连接后重连
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture futureListener) throws Exception {
                if (futureListener.isSuccess()) {
                    channel = futureListener.channel();
                    System.out.println(String.format("Connect to server successfully! %s",channel.id()));
                    sendTestData();
                } else {
                    System.out.println("Failed to connect to server, try connect after 10s");
                    futureListener.channel().eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
                            connect();
                        }
                    }, 10, TimeUnit.SECONDS);
                }
            }
        });
    }


    public void sendTestData()
    {
        if (channel != null && channel.isActive()) {
            Protocol pro = new Protocol();
            pro.setRequestId(System.currentTimeMillis());
            pro.setLength(999999999);
            pro.setType(MessageType.REQ.value());
            Message message = new Message();
            message.setClassName(String.format("CLASSS%d", 10));
            message.setMethodName(String.format("METHOD%d", 10));
            pro.setBody(message);
            ByteBuf sendBuf = Unpooled.buffer();



            sendBuf.writeInt(pro.getVersion());
            //写入报文长度，非真实长度，先占位
            sendBuf.writeInt(pro.getLength());
            sendBuf.writeLong(pro.getRequestId());
            sendBuf.writeByte(pro.getType());
            if (pro.getBody() != null) {
                sendBuf.writeBytes(pro.serializeToByte());
            }

            //更新真实报文长度到占位符
            System.out.println("CLIENT发送报文");
            sendBuf.setInt(4, sendBuf.readableBytes() - Protocol.HEAD_SIZE - Protocol.PROTOCOL_LENGTH);
            //编码完成
            channel.writeAndFlush(sendBuf);
        } else {
            System.out.println("CLIENT发送报文失败");
        }

    }

    public static void main(String[] args)  {
       // Client client = new Client(args[1],Integer.parseInt(args[2]));
        Client client = new Client("127.0.0.1",12701);
        client.start();
        //client.sendTestData();
//        new Thread(new Runnable() {
//            @Override
//            public void run()  {
//                try {
//                    System.out.println("过30秒后，发送一个正常报文");
//                    Thread.sleep(30000);
//                    client.sendTestData();
//                }catch (Exception e) {
//                    System.out.println(e.getMessage());
//                }
//
//            }
//        }).start();



    }
}
