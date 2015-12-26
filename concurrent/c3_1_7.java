import java.util.concurrent.locks.*;
import java.util.concurrent.*;
import java.util.Random;

public class c3_1_7 {
	public static Object u = new Object();
	static COT t1 = new COT("t1");
	static COT t2 = new COT("t2");
	public static class COT extends Thread {
		public COT(String n) { super.setName(n); }
		public void run() {
			synchronized(u) {
				System.out.println("in " + getName());
				LockSupport.park();
				if (Thread.interrupted()) {
					System.out.println(getName() + " interrupted");
				} else {
					System.out.println(getName() + " done");
				}
			}
		}
	}
	public static void main(String[] args) throws InterruptedException {
		System.out.println("start t1");
		t1.start();
		
		Thread.sleep(100);

		System.out.println("start t2");
		t2.start();

		System.out.println("unpark t1");
		LockSupport.unpark(t1);

		//System.out.println("interrupt t1");
		//t1.interrupt();

		System.out.println("unpark t2");
		LockSupport.unpark(t2);

		System.out.println("join t1");
		t1.join();

		System.out.println("join t2");
		t2.join();
	}
}