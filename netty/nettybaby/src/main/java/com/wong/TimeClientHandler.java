package com.wong;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import java.util.logging.Logger;

public class TimeClientHandler extends ChannelHandlerAdapter {
	private static final Logger logger = Logger.getLogger(TimeClientHandler.class.getName());
	private int counter;
	private byte[] req;
	//private final ByteBuf firstMessage;
	public TimeClientHandler() {
		req = ("time" + System.getProperty("line.separator")).getBytes();
		// byte[] req = "time".getBytes();
		// firstMessage = Unpooled.buffer(req.length);
		// firstMessage.writeBytes(req);
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		ByteBuf message = null;
		for (int i=0; i<100; ++i) {
			message = Unpooled.buffer(req.length);
			message.writeBytes(req);
			ctx.writeAndFlush(message);
		}
		//ctx.writeAndFlush(firstMessage);
	}
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// ByteBuf buf = (ByteBuf)msg;
		// byte[] req = new byte[buf.readableBytes()];
		// buf.readBytes(req);
		// String body = new String(req, "UTF-8");
		String body = (String)msg;
		System.out.println(body + " : counter " + ++counter);
		//ctx.close();
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.warning(cause.getMessage());
		ctx.close();
	}
}