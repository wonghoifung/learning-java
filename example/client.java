import java.util.*;
import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;

public class client
{
	private Selector selector = null;
	static final int PORT = 30000;
	private Charset charset = Charset.forName("UTF-8");
	private SocketChannel sc = null;
	public void init()throws IOException
	{
		selector = Selector.open();
		InetSocketAddress isa = new InetSocketAddress("127.0.0.1", PORT);
		sc = SocketChannel.open(isa);
		sc.configureBlocking(false);
		sc.register(selector, SelectionKey.OP_READ);
		new ClientThread().start();
		Scanner scan = new Scanner(System.in);
		while (scan.hasNextLine())
		{
			String line = scan.nextLine();
			sc.write(charset.encode(line));
		}
	}

	private class ClientThread extends Thread
	{
		public void run()
		{
			try
			{
				while (selector.select() > 0)  
				{
					for (SelectionKey sk : selector.selectedKeys())
					{
						selector.selectedKeys().remove(sk);
						if (sk.isReadable())
						{
							SocketChannel sc = (SocketChannel)sk.channel();
							ByteBuffer buff = ByteBuffer.allocate(1024);
							String content = "";

							int r = sc.read(buff);
							if(r==0)continue;
							if(r==-1)
							{
								sc.close();
								sk.cancel();
								continue;
							}

							while(r > 0)
							{
								sc.read(buff); 
								buff.flip();
								content += charset.decode(buff);
								r = sc.read(buff);
								if(r==-1)
								{
									sc.close();
									sk.cancel();
									break;
								}
							}
							System.out.println("chat content: " + content);
							sk.interestOps(SelectionKey.OP_READ);
						}
					}
				}
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
	}
	public static void main(String[] args)
		throws IOException
	{
		new client().init();
	}
}
