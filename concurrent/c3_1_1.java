import java.util.concurrent.locks.ReentrantLock;

public class c3_1_1 implements Runnable {
	public static ReentrantLock lock1 = new ReentrantLock();
	public static ReentrantLock lock2 = new ReentrantLock();
	int lock;
	public c3_1_1(int lock) {
		this.lock = lock;
	}
	public void run() {
		try {
			if (lock == 1) {
				lock1.lockInterruptibly();
				try {Thread.sleep(500);} catch(InterruptedException e) {}
				lock2.lockInterruptibly();
			} else {
				lock2.lockInterruptibly();
				try {Thread.sleep(500);} catch(InterruptedException e) {}
				lock1.lockInterruptibly();
			}
			System.out.println(Thread.currentThread().getId() + ": get all locks");
		} catch (InterruptedException e) {
			//e.printStackTrace();
		} finally {
			if (lock1.isHeldByCurrentThread()) lock1.unlock();
			if (lock2.isHeldByCurrentThread()) lock2.unlock();
			System.out.println(Thread.currentThread().getId() + ": quit");
		}
	}
	public static void main(String[] args) throws InterruptedException {
		c3_1_1 r1 = new c3_1_1(1);
		c3_1_1 r2 = new c3_1_1(2);
		Thread t1 = new Thread(r1);
		Thread t2 = new Thread(r2);
		t1.start();
		t2.start();
		Thread.sleep(1000);
		t2.interrupt();
	}
}