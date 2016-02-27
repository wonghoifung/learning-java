import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class nioTimeClient {
	public static void main(String[] args) {
		String ip = "127.0.0.1";
		int port = 8080;
		if (args != null && args.length > 1) {
			ip = args[0];
			try {
				port = Integer.valueOf(args[1]);
			} catch (NumberFormatException e) {
				// 8080
			}
		}
		new Thread(new TimeClientHandle(ip,port), "nioTimeClient").start();
	}
	public static class TimeClientHandle implements Runnable {
		private String host;
		private int port;
		private Selector selector;
		private SocketChannel socketChannel;
		private volatile boolean stop;
		public TimeClientHandle(String host, int port) {
			this.host = host == null ? "127.0.0.1" : host;
			this.port = port;
			try {
				selector = Selector.open();
				socketChannel = SocketChannel.open();
				socketChannel.configureBlocking(false);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		@Override
		public void run() {
			try {
				doConnect();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
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
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(1);
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
				SocketChannel sc = (SocketChannel)key.channel();
				if (key.isConnectable()) {
					if (sc.finishConnect()) {
						sc.register(selector, SelectionKey.OP_READ);
						doWrite(sc);
					} else {
						System.exit(1);
					}
				}
				if (key.isReadable()) {
					ByteBuffer readBuffer = ByteBuffer.allocate(1024);
					int readBytes = sc.read(readBuffer);
					if (readBytes > 0) {
						readBuffer.flip();
						byte[] bytes = new byte[readBuffer.remaining()];
						readBuffer.get(bytes);
						String body = new String(bytes, "UTF-8");
						System.out.println(body);
						this.stop = true;
					} else if (readBytes < 0) {
						key.cancel();
						sc.close();
					} else {

					}
				}
			}
		}
		private void doConnect() throws IOException {
			if (socketChannel.connect(new InetSocketAddress(host, port))) {
				socketChannel.register(selector, SelectionKey.OP_READ);
				doWrite(socketChannel);
			} else {
				socketChannel.register(selector, SelectionKey.OP_CONNECT);
			}
		}
		private void doWrite(SocketChannel sc) throws IOException {
			byte[] req = "time".getBytes();
			ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
			writeBuffer.put(req);
			writeBuffer.flip();
			sc.write(writeBuffer);
			if (!writeBuffer.hasRemaining()) {
				System.out.println("send req succeed");
			}
		}
	}
}