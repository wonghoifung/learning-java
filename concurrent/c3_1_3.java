import java.util.concurrent.locks.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class c3_1_3 implements Runnable {
	final Semaphore semp = new Semaphore(5);
	public void run() {
		try {
			semp.acquire();
			Thread.sleep(2000);
			System.out.println(Thread.currentThread().getId() + " done");
			semp.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		ExecutorService exec = Executors.newFixedThreadPool(20);
		final c3_1_3 demo = new c3_1_3();
		for (int i=0; i<20; ++i) {
			exec.submit(demo);
		}
	}
}