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

public class TimeServer 
{
	public void bind(int port) throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 1024)
				.childHandler(new ChildChannelHandler());
			ChannelFuture f = b.bind(port).sync();
			f.channel().closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	private static class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
		@Override
		protected void initChannel(SocketChannel arg0) throws Exception {
			arg0.pipeline().addLast(new TimeServerHandler());
		}
	}
    public static void main( String[] args ) throws Exception
    {
        int port = 8080;
        if (args != null && args.length > 0) {
        	try {
        		port = Integer.valueOf(args[0]);
        	} catch (NumberFormatException e) {

        	}
        }
        new TimeServer().bind(port);
    }
    public static class TimeServerHandler extends ChannelHandlerAdapter {
    	@Override
    	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    		ByteBuf buf = (ByteBuf)msg;
    		byte[] req = new byte[buf.readableBytes()];
    		buf.readBytes(req);
    		String body = new String(req, "UTF-8");
    		System.out.println(body);
    		String currentTime = "bad";
    		if ("time".equalsIgnoreCase(body)) {
    			currentTime = new java.util.Date(System.currentTimeMillis()).toString();
    		}
    		ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
    		ctx.write(resp);
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
}
