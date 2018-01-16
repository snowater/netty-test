package com.snow.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

public class ClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client send msg");
        // 下面这种直接发送数据的方式是不行的
//		ctx.writeAndFlush("Netty Rocks!".getBytes());

        // 用HeapBuf发送数据，server发回来仍是DirectBuf
//		ByteBuf heapBuf = Unpooled.buffer(20);
//		heapBuf.writeBytes("Netty Rocks!".getBytes());
//		ctx.writeAndFlush(heapBuf);

        // 使用DirectBuf发送数据
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        System.out.println("Client received: " + ByteBufUtil.hexDump(msg.readBytes(msg.readableBytes())));
        // 只有是Heap Buffer才能如此访问数据
        // String data = new String(msg.array());
        // Direct Buffer访问数据方式
        msg.readerIndex(0); // 上一语句的读操作已经将readerIndex移动到“Netty Rocks!”之后了，所以需要调回来
        // 而下面的读取方式是一种间接的数据读取，不影响readerIndex
        byte[] arr = new byte[msg.readableBytes()];
        msg.getBytes(0, arr);
        String data = new String(arr);
        System.out.println("Client received:" + data);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}