import java.util.concurrent.locks.*;

public class c3_1_2 implements Runnable {
	public static ReentrantLock lock = new ReentrantLock();
	public static Condition condition = lock.newCondition();
	public void run() {
		try {
			lock.lock();
			condition.await();
			System.out.println("......");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
	public static void main(String[] args) throws InterruptedException {
		c3_1_2 r1 = new c3_1_2();
		Thread t1 = new Thread(r1);
		t1.start();
		Thread.sleep(1000);
		lock.lock();
		condition.signal();
		lock.unlock();
	}
}