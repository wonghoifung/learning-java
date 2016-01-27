import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.*;

public class c5_6 {

	public static class Msg {
		public double i;
		public double j;
		public String expr=null;
	}

	public static class Plus implements Runnable {
		public static BlockingQueue<Msg> bq = new LinkedBlockingQueue<Msg>();
		public void run() {
			while (true) {
				try {
					Msg msg = bq.take();
					msg.j = msg.i + msg.j;
					Multiply.bq.add(msg);
				} catch (InterruptedException e) {}
			}
		}
	}

	public static class Multiply implements Runnable {
		public static BlockingQueue<Msg> bq = new LinkedBlockingQueue<Msg>();
		public void run() {
			while (true) {
				try {
					Msg msg = bq.take();
					msg.i = msg.i * msg.j;
					Div.bq.add(msg);
				} catch (InterruptedException e) {}
			}
		}
	}

	public static class Div implements Runnable {
		public static BlockingQueue<Msg> bq = new LinkedBlockingQueue<Msg>();
		public void run() {
			while (true) {
				try {
					Msg msg = bq.take();
					msg.i = msg.i / 2;
					System.out.println(msg.expr + " = " + msg.i);
				} catch (InterruptedException e) {}
			}
		}
	}

	public static void main(String[] args) {
		new Thread(new Plus()).start();
		new Thread(new Multiply()).start();
		new Thread(new Div()).start();

		for (int i=1; i<=1000; ++i) {
			for (int j=1; j<=1000; ++j) {
				Msg msg = new Msg();
				msg.i = i;
				msg.j = j;
				msg.expr = "((" + i + "+" + j + ")*" + i + ")/2";
				Plus.bq.add(msg);
			}
		}
	}

}