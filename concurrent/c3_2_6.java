import java.util.concurrent.*;

public class c3_2_6 {

	public static class MyTask implements Runnable {
		public String name;
		public MyTask(String name) {
			this.name = name;
		}
		public void run() {
			System.out.println("running TID: " + Thread.currentThread().getId() + ", task: " + name);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		ExecutorService es = new ThreadPoolExecutor(5, 5, 0L, TimeUnit.MILLISECONDS, 
			new LinkedBlockingQueue<Runnable>()) {

			protected void beforeExecute(Thread t, Runnable r) {
				System.out.println("before " + ((MyTask)r).name);
			}

			protected void afterExecute(Runnable r, Throwable t) {
				System.out.println("after " + ((MyTask)r).name);
			}

			protected void terminated() {
				System.out.println("terminate ");
			}
		};

		for (int i=0; i<5; ++i) {
			MyTask task = new MyTask("TASK_"+i);
			es.execute(task);
			Thread.sleep(10);
		}
		es.shutdown();
	}
}