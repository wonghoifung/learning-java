import java.util.*;
import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;

public class frog_stress_client implements parse_callback
{
	private Selector selector = null;
	static final int PORT = 9878;
	private SocketChannel sc = null;

	public int onMessage(frog_codec msg)
	{
		int cmd = msg.command();
		int i = msg.readInt();
		String s = msg.readString();
		int i2 = msg.readInt();
		System.out.println(i+" "+s+" "+i2);
		return 0;
	}

	public void init()throws IOException
	{
		selector = Selector.open();
		InetSocketAddress isa = new InetSocketAddress("127.0.0.1", PORT);
		sc = SocketChannel.open(isa);
		sc.configureBlocking(false);
		sc.register(selector, SelectionKey.OP_READ);
		ClientThread thread = new ClientThread();
		thread.setParseCallback(this);
		thread.start();

		frog_codec op = new frog_codec();
		op.begin((short)(123));
		op.writeInt(1234567);
		op.writeShort((short)(321));
		op.writeString("hello world!!!");
		op.writeShort((short)(321));
		op.writeInt(1234567);
		op.end();
/*
		int csize = 9 + 4 + 2 + 4 + 15 + 2 + 4;
		System.out.println("csize:"+csize);
		System.out.println("op size:"+op.size());
		String ss = new String();int lnc=0;
		for(int i=0;i<op.size();++i){
			ss += (op.buffer()[i] + " ");
			lnc++;
			if(lnc%8==0)ss+="\n";
		}
		System.out.println(ss);
*/
		byte tmp[] = new byte[op.size()];
		for(int i=0;i<op.size();++i){
			tmp[i] = op.buffer()[i];
		}
		ByteBuffer buf = ByteBuffer.wrap(tmp);
		sc.write(buf);
	}

	private class ClientThread extends Thread
	{
		private parse_callback cb;
		public void setParseCallback(parse_callback c){cb=c;}

		public void run()
		{
			frog_parser parser = new frog_parser();
			parser.setParseCallback(cb);
			parser.init();

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
							ByteBuffer buff = ByteBuffer.allocate(10240);
							String content = "";

							int r = sc.read(buff);
							if(r==0)continue;
							if(r==-1)
							{
								sc.close();
								sk.cancel();
								continue;
							}
		
							buff.flip();	
							byte[] bytes = new byte[buff.remaining()];
							buff.get(bytes,0,bytes.length);
							parser.parse(bytes,bytes.length);
							
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
		frog_stress_client client = new frog_stress_client();
		client.init();
	}
}

