import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetSocketAddress; 
import java.nio.channels.Selector; 
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.lang.*;
import java.io.*;

public class nioTimeServer {
	public static void main(String[] args) throws IOException {
		int port = 8080;
		if (args != null && args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {

			}
		}
		MultiplexerTimerServer timeServer = new MultiplexerTimerServer(port);
		new Thread(timeServer, "nio").start();
	}

	public static class MultiplexerTimerServer implements Runnable {
		private Selector selector;
		private ServerSocketChannel servChannel;
		private volatile boolean stop;
		public MultiplexerTimerServer(int port) {
			try {
				selector = Selector.open();
				servChannel = ServerSocketChannel.open();
				servChannel.configureBlocking(false);
				servChannel.socket().bind(new InetSocketAddress(port), 1024);
				servChannel.register(selector, SelectionKey.OP_ACCEPT);
				System.out.println("nioTimeServer listen on " + port);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		public void stop() {
			this.stop = true;
		}
		@Override
		public void run() {
			while (!stop) {
				try {
					selector.select(1000);
					Set<SelectionKey> selectedKeys = selector.selectedKeys();
					Iterator<SelectionKey> it = selectedKeys.iterator();
					SelectionKey key = null;
					while (it.hasNext()) {
						key = it.next();
						it.remove();
						try {
							handleInput(key);
						} catch (Exception e) {
							if (key != null) {
								key.cancel();
								if (key.channel() != null) key.channel().close();
							}
						}
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			if (selector != null) {
				try {
					selector.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		private void handleInput(SelectionKey key) throws IOException {
			if (key.isValid()) {
				if (key.isAcceptable()) {
					ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
					SocketChannel sc = ssc.accept();
					sc.configureBlocking(false);
					//sc.socket().setTcpNoDelay(true);
					sc.register(selector, SelectionKey.OP_READ);
				}
				if (key.isReadable()) {
					SocketChannel sc = (SocketChannel)key.channel();
					ByteBuffer readBuffer = ByteBuffer.allocate(1024);
					int readBytes = sc.read(readBuffer);
					if (readBytes > 0) {
						readBuffer.flip();
						byte[] bytes = new byte[readBuffer.remaining()];
						readBuffer.get(bytes);
						String body = new String(bytes, "UTF-8").trim();
						System.out.println("receive " + body + ", size " + body.length());
						String currentTime = "bad";
						if ("time".equalsIgnoreCase(body)) {
							currentTime = new java.util.Date(System.currentTimeMillis()).toString();
						}
						//System.out.println("send back " + currentTime);
						doWrite(sc, currentTime + '\n');
					} else if (readBytes < 0) {
						key.cancel();
						sc.close();
					} else {

					}
				}
			}
		}
		private void doWrite(SocketChannel channel, String response) throws IOException {
			if (response != null && response.trim().length() > 0) {
				byte[] bytes = response.getBytes();
				ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
				writeBuffer.put(bytes);
				writeBuffer.flip();
				System.out.println("write " + writeBuffer);
				channel.write(writeBuffer);
			}
		}
	}
}
