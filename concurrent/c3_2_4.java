import java.util.concurrent.locks.*;
import java.util.concurrent.*;

public class c3_2_4 {

	public static class Task implements Runnable {
		public void run() {
			System.out.println(System.currentTimeMillis() + ":TID:" + Thread.currentThread().getId());
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		Task t = new Task();
		
		ExecutorService es = new ThreadPoolExecutor(5, 5, 0L, TimeUnit.MILLISECONDS, 
			//new LinkedBlockingQueue<Runnable>(10), 
			new SynchronousQueue<Runnable>(),
			Executors.defaultThreadFactory(),
			new RejectedExecutionHandler() {
				public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
					System.out.println(r.toString() + " is discarded");
				}
			});

		for (int i=0; i<Integer.MAX_VALUE; ++i) {
			es.submit(t);
			Thread.sleep(10);
		}
	}

}