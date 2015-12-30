import java.util.concurrent.*;

public class c3_2_8 {

	public static class TraceThreadPoolExecutor extends ThreadPoolExecutor {
		public TraceThreadPoolExecutor(int corePoolSize, int maximumPoolSize, 
			long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		}
		public void execute(Runnable task) {
			super.execute(wrap(task, clientTrace(), Thread.currentThread().getName()));
		}
		public Future<?> submit(Runnable task) {
			return super.submit(wrap(task, clientTrace(), Thread.currentThread().getName()));
		}
		private Exception clientTrace() {
			return new Exception("client stack trace");
		}
		private Runnable wrap(final Runnable task, final Exception clientStack, String clientThreadName) {
			return new Runnable() {
				public void run() {
					try {
						task.run();
					} catch (Exception e) {
						clientStack.printStackTrace();
						throw e;
					}
				}
			};
		}
	}

	public static class DivTask implements Runnable {
		int a,b;
		public DivTask(int a, int b) {
			this.a = a;
			this.b = b;
		}
		public void run() {
			double re = a/b;
			System.out.println(re);
		}
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ThreadPoolExecutor pools = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 0L, 
			TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
		
		//ThreadPoolExecutor pools = new TraceThreadPoolExecutor(0, Integer.MAX_VALUE, 0L, 
		//	TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

		for (int i=0; i<5; ++i) {
			//pools.submit(new DivTask(100,i));
			pools.execute(new DivTask(100,i));
		}
	}
}