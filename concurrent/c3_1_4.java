import java.util.concurrent.locks.*;
import java.util.Random;

public class c3_1_4 {
	private static Lock lock = new ReentrantLock();
	private static ReentrantReadWriteLock rwlock = new ReentrantReadWriteLock();
	private static Lock readlock = rwlock.readLock();
	private static Lock writelock = rwlock.writeLock();
	private int value;
	public Object handleRead(Lock lock) throws InterruptedException {
		try {
			lock.lock();
			Thread.sleep(1000);
			return value;
		} finally {
			lock.unlock();
		}
	}
	public void handleWrite(Lock lock, int index) throws InterruptedException {
		try {
			lock.lock();
			Thread.sleep(1000);
			value = index;
		} finally {
			lock.unlock();
		} 
	}
	public static void main(String[] args) {
		final c3_1_4 demo = new c3_1_4();
		Runnable rr = new Runnable() {
			public void run() {
				try {
					//demo.handleRead(readlock);
					demo.handleRead(lock);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		Runnable wr = new Runnable() {
			public void run() {
				try {
					//demo.handleWrite(writelock, new Random().nextInt());
					demo.handleWrite(lock, new Random().nextInt());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		for (int i=0; i<18; ++i) {
			new Thread(rr).start();
		}
		for (int i=18; i<20; ++i) {
			new Thread(wr).start();
		}
	}
}