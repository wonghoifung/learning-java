import java.util.concurrent.locks.*;
import java.util.concurrent.*;
import java.util.Random;

public class c3_1_6 {

	public static class Soldier implements Runnable {
		private String soldier;
		private final CyclicBarrier cyclic;
		Soldier(CyclicBarrier c, String n) {
			this.cyclic = c;
			this.soldier = n;
		}
		public void run() {
			try {
				cyclic.await();
				doWork();
				cyclic.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (BrokenBarrierException e) {
				e.printStackTrace();
			}
		}
		void doWork() {
			try {
				Thread.sleep(Math.abs(new Random().nextInt()%10000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(soldier + " job done");
		}
	}

	public static class BarrierRun implements Runnable {
		boolean flag;
		int N;
		public BarrierRun(boolean f, int n) {
			this.flag = f;
			this.N = n;
		}
		public void run() {
			if (flag) { System.out.println(N + " soldiers job done"); }
			else { System.out.println(N + " soldiers ready"); flag = true; }
		}
	}

	public static void main(String[] args) throws InterruptedException {
		final int N = 10;
		boolean flag = false;
		CyclicBarrier cyclic = new CyclicBarrier(N, new BarrierRun(flag, N));
		Thread[] all = new Thread[N];
		for (int i=0; i<N; ++i) {
			System.out.println("soldier " + i + " ready");
			all[i] = new Thread(new Soldier(cyclic, "soldier" + i));
			all[i].start();
			/*if (i == 5) {
				all[0].interrupt();
			}*/
		}
	}

}
