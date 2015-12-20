/*
$ jps  
  3076 Jps  
  448 DeadLock  
$ jstack -l 448 > deadlock.jstack 
*/

public class c2_2_5 {
	/*
	public static Object u = new Object();
	static COT t1 = new COT("t1");
	static COT t2 = new COT("t2");
	public static class COT extends Thread {
		public COT(String name) {
			super.setName(name);
		}
		public void run() {
			synchronized(u) {
				System.out.println("suspend " + getName());
				Thread.currentThread().suspend();
			}
		}
	}
	public static void main(String[] args) throws InterruptedException {
		System.out.println("start t1");
		t1.start();
		Thread.sleep(1000);

		System.out.println("start t2");
		t2.start();
		//Thread.sleep(1000);

		System.out.println("resume t1");
		t1.resume();
		//Thread.sleep(1000);

		System.out.println("resume t2");
		t2.resume();

		System.out.println("join t1");
		t1.join();

		System.out.println("join t2");
		t2.join();
	}
	*/
	public static Object u = new Object();
	public static class WriteThread extends Thread {
		volatile boolean suspendme = false;
		public void suspendMe() {
			suspendme = true;
		}
		public void resumeMe() {
			suspendme = false;
			synchronized (this) {
				notify();
			}
		}
		public void run() {
			while (true) {
				synchronized (this) {
					while (suspendme) {
						try {
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				synchronized (u) {
					System.out.println("in WriteThread");
				}
				Thread.yield();
			}
		}
	}
	public static class ReadThread extends Thread {
		public void run() {
			while (true) {
				synchronized (u) {
					System.out.println("in ReadThread");
				}
				Thread.yield();
			}
		}
	}
	public static void main(String[] args) throws InterruptedException {
		WriteThread t1 = new WriteThread();
		ReadThread t2 = new ReadThread();
		t1.start();
		t2.start();
		Thread.sleep(1000);
		t1.suspendMe();
		System.out.println("suspend t1 2 sec");
		Thread.sleep(2000);
		System.out.println("reusme t1");
		t1.resumeMe();
	}
}