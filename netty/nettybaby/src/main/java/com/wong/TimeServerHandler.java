package com.wong;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioServerSocketChannel; 
import io.netty.channel.EventLoopGroup;
import io.netty.bootstrap.ServerBootstrap; 
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;

public class TimeServerHandler extends ChannelHandlerAdapter {
	private int counter;
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// ByteBuf buf = (ByteBuf)msg;
		// byte[] req = new byte[buf.readableBytes()];
		// buf.readBytes(req);
		// String body = new String(req, "UTF-8");
		// System.out.println(body);
		// String currentTime = "bad";
		// if ("time".equalsIgnoreCase(body)) {
		// 	currentTime = new java.util.Date(System.currentTimeMillis()).toString();
		// }
		// ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
		// ctx.write(resp);

		// String body = new String(req, "UTF-8").substring(0, req.length - System.getProperty("line.separator").length());
		String body = (String)msg;
		System.out.println(body + " : counter " + ++counter);
		String currentTime = "bad";
		if ("time".equalsIgnoreCase(body)) {
			currentTime = new java.util.Date(System.currentTimeMillis()).toString();
		}
		currentTime = currentTime + System.getProperty("line.separator");
		ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
		ctx.writeAndFlush(resp);
	}
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		ctx.close();
	}
}
