import java.util.concurrent.*;
import java.util.*;

public class c3_2_9 extends RecursiveTask<Long> {

	private static final int THRESHOLD = 10;

	private long start;

	private long end;

	public c3_2_9(long start, long end) {
		this.start = start;
		this.end = end;
	}

	public Long compute() {
		long sum = 0;
		boolean canCompute = (end - start) < THRESHOLD;
		if (canCompute) {
			for (long i=start; i<=end; ++i) {
				sum += i;
			}
		} else {
			//long step = (start + end) / 100;
			long step = (end - start) / 100;
			System.out.println("step:"+step);
			ArrayList<c3_2_9> subTasks = new ArrayList<c3_2_9>();
			long pos = start;
			for (int i=0; i<100; ++i) {
				long lastOne = pos+step;
				if (lastOne>end) lastOne=end;
				c3_2_9 subTask = new c3_2_9(pos,lastOne);
				pos = lastOne + 1;
				subTasks.add(subTask);
				subTask.fork();
			}
			for (c3_2_9 t:subTasks) {
				sum += t.join();
			}
		}
		return sum;
	}

	public static void main(String[] args) {
		ForkJoinPool forkJoinPool = new ForkJoinPool();
		c3_2_9 task = new c3_2_9(0, 100000);
		ForkJoinTask<Long> result = forkJoinPool.submit(task);
		try {
			long res = result.get();
			System.out.println("sum="+res);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

}