//package com.wong;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.lang.*;
import java.io.*;

public class blockTimeClient {
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
		Socket socket = null;
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			socket = new Socket(ip, port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			out.println("time");
			System.out.println("request time server...");
			String resp = in.readLine();
			System.out.println("receive: " + resp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
				out = null;
			}
			if (in != null) {
				try { in.close(); } catch(IOException ee) { ee.printStackTrace(); }
				in = null;
			}
			if (socket != null) {
				try { socket.close(); } catch (IOException eee) { eee.printStackTrace(); }
				socket = null;
			}
		}
	}
}