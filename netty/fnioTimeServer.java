//package com.wong;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ThreadPoolExecutor; 
import java.util.concurrent.ArrayBlockingQueue; 
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.lang.*;
import java.io.*;

public class fnioTimeServer {
	public static void main(String[] args) throws IOException {
		int port = 8080;
		if (args != null && args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {

			}
		}
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
			System.out.println("fnioTimeServer listen on: " + port);
			Socket socket = null;
			TimeServerHandlerExecutePool singleExecutor = new TimeServerHandlerExecutePool(50,10000);
			while (true) {
				socket = server.accept();
				singleExecutor.execute(new TimeServerHandler(socket));
			}
		} finally {
			if (server != null) {
				System.out.println("fnioTimeServer close");
				server.close();
				server = null;
			}
		}
	}

	public static class TimeServerHandlerExecutePool {
		private ExecutorService executor;
		public TimeServerHandlerExecutePool(int maxPoolSize, int queueSize) {
			executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), maxPoolSize, 120L, TimeUnit.SECONDS,
				new ArrayBlockingQueue<java.lang.Runnable>(queueSize));
		}
		public void execute(java.lang.Runnable task) {
			executor.execute(task);
		}
	}

	public static class TimeServerHandler implements Runnable {
		private Socket socket;
		public TimeServerHandler(Socket socket) {
			this.socket = socket;
		}
		@Override
		public void run() {
			BufferedReader in = null;
			PrintWriter out = null;
			try {
				in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
				out = new PrintWriter(this.socket.getOutputStream(), true);
				String body = null;
				while (true) {
					body = in.readLine();
					if (body == null) break;
					System.out.println("the time server receive: " + body);
					if ("time".equalsIgnoreCase(body)) {
						out.println(new java.util.Date(System.currentTimeMillis()).toString());
					} else {
						out.println("bad");
					}
				}
			} catch (Exception e) {
				if (in != null) {
					try { in.close(); } catch(IOException ee) { ee.printStackTrace(); }
					in = null;
				}
				if (out != null) {
					out.close();
					out = null;
				}
				if (this.socket != null) {
					try { this.socket.close(); } catch (IOException eee) { eee.printStackTrace(); }
					this.socket = null;
				}
			}
		}
	}
}
