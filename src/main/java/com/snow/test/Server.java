package com.snow.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class Server {
    private final int port;

    public Server(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // create ServerBootstrap instance
            ServerBootstrap b = new ServerBootstrap();
            // Specifies NIO transport, local socket address
            // Adds handler to channel pipeline
            b.group(group).channel(NioServerSocketChannel.class).localAddress(new InetSocketAddress(port)) // 设置监听端口
                            .childHandler(new ChannelInitializer<Channel>() { // 有连接到达是会创建一个channel
                                @Override
                                protected void initChannel(Channel ch) throws Exception {
                                    // pipeline管理channel中的Handler，在channel队列中添加一个handler来处理业务
                                    ch.pipeline().addLast(new ServerHandler());
                                }
                            });

            // bind server, waits for server to close, and release resources
            ChannelFuture f = b.bind().sync();
            System.out.println(Server.class.getName() + " started and listen on " + f.channel().localAddress());
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {
        new Server(8000).start();
    }
}
