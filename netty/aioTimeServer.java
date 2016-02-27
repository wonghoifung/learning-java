import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

public class aioTimeServer {
	public static void main(String[] args) throws IOException {
		int port = 8080;
		if (args != null && args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {

			}
		}
		AsyncTimeServerHandler timeServer = new AsyncTimeServerHandler(port);
		new Thread(timeServer, "aioTimeServer").start();
	}

	public static class AsyncTimeServerHandler implements Runnable {
		private int port;
		CountDownLatch latch;
		AsynchronousServerSocketChannel asynchronousServerSocketChannel;
		public AsyncTimeServerHandler(int port) {
			this.port = port;
			try {
				asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
				asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
				System.out.println("aioTimeServer listen on " + port);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		@Override
		public void run() {
			latch = new CountDownLatch(1);
			doAccept();
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		public void doAccept() {
			asynchronousServerSocketChannel.accept(this, new AcceptCompletionHandler());
		}
	}
	public static class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsyncTimeServerHandler> {
		@Override
		public void completed(AsynchronousSocketChannel result, AsyncTimeServerHandler attachment) {
			attachment.asynchronousServerSocketChannel.accept(attachment, this);
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			result.read(buffer, buffer, new ReadCompletionHandler(result));
		}
		@Override
		public void failed(Throwable exc, AsyncTimeServerHandler attachment) {
			exc.printStackTrace();
			attachment.latch.countDown();
		}
	}
	public static class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
		private AsynchronousSocketChannel channel;
		public ReadCompletionHandler(AsynchronousSocketChannel channel) {
			if (this.channel == null) this.channel = channel;
		}
		@Override 
		public void completed(Integer result, ByteBuffer attachment) {
			attachment.flip();
			byte[] body = new byte[attachment.remaining()];
			attachment.get(body);
			try {
				String req = new String(body, "UTF-8");
				System.out.println("receive " + req);
				String currentTime = "bad";
				if ("time".equalsIgnoreCase(req)) {
					currentTime = new java.util.Date(System.currentTimeMillis()).toString();
					doWrite(currentTime);
				} 
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		private void doWrite(String currentTime) {
			if (currentTime != null && currentTime.trim().length() > 0) {
				byte[] bytes = (currentTime).getBytes();
				ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
				writeBuffer.put(bytes);
				writeBuffer.flip();
				channel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer,ByteBuffer>() {
					@Override
					public void completed(Integer result, ByteBuffer buffer) {
						if (buffer.hasRemaining()) channel.write(buffer, buffer, this);
					}
					@Override
					public void failed(Throwable exc, ByteBuffer attachment) {
						try {
							channel.close();
						} catch (IOException e) {

						}
					}
				});
			}
		}
		@Override
		public void failed(Throwable exc, ByteBuffer attachment) {
			try {
				this.channel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}