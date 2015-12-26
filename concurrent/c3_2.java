import java.util.concurrent.locks.*;
import java.util.concurrent.*;
import java.util.Random;

public class c3_2 {
	public static void main(String[] args) {
		ScheduledExecutorService ses = Executors.newScheduledThreadPool(10);
		ses.scheduleAtFixedRate(new Runnable() {
			public void run() {
				try {
					Thread.sleep(5000);
					System.out.println(System.currentTimeMillis() / 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, 0, 2, TimeUnit.SECONDS);
	}
}