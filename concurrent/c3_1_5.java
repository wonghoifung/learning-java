import java.util.concurrent.locks.*;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;

public class c3_1_5 implements Runnable {
	static final CountDownLatch end = new CountDownLatch(10);
	static final c3_1_5 r = new c3_1_5();
	public void run() {
		try {
			Thread.sleep(new Random().nextInt(10)*1000);
			System.out.println("check done");
			end.countDown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws InterruptedException {
		ExecutorService exec = Executors.newFixedThreadPool(10);
		for (int i=0; i<10; ++i) {
			exec.submit(r);
		}
		end.await();
		System.out.println("go");
		exec.shutdown();
	}
}