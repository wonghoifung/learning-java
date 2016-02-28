package com.wong;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
// import java.util.ArrayList;
// import java.util.List;

public class SubReqClientHandler extends ChannelHandlerAdapter {
	public SubReqClientHandler() {

	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		for (int i=0; i<10; ++i) {
			ctx.write(subReq(i));
		}
		ctx.flush();
	}

	private SubscribeReqProto.SubscribeReq subReq(int i) {
		SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();
		builder.setSubReqID(i);
		builder.setUserName("wong");
		builder.setProductName("netty");
		builder.setAddress("address " + i);
		// List<String> address = new ArrayList<String>();
		// address.add("ab");
		// address.add("cd");
		// address.add("xyz");
		// builder.addAllAddress(address);
		return builder.build();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("resp: [" + msg + "]");
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	} 
}