import java.util.concurrent.*;
import java.util.*;
import java.text.*;

public class c4_3_2 {

	static volatile ThreadLocal<SimpleDateFormat> t1 = new ThreadLocal<SimpleDateFormat>() {
		protected void finalize() throws Throwable {
			System.out.println(this.toString() + " is gc...");
		}
	};

	static final int num = 10;
	static volatile CountDownLatch cd = new CountDownLatch(num);

	public static class ParseDate implements Runnable {
		int i = 0;
		public ParseDate(int i) {
			this.i = i;
		}
		public void run() {
			try {
				if (t1.get() == null) {
					t1.set(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") {
						protected void finalize() throws Throwable {
							System.out.println(this.toString() + " is gc!!!");
						}
					});
					System.out.println(Thread.currentThread().getId() + ":create SimpleDateFormat");
				}
				Date t = t1.get().parse("2016-01-11 8:39:" + i % 60);
			} catch (ParseException e) {
				e.printStackTrace();
			} finally {
				t1.remove();
				cd.countDown();
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		ExecutorService es = Executors.newFixedThreadPool(10);
		// for (int i=0; i<num; i++) {
		// 	es.execute(new ParseDate(i));
		// }
		// cd.await();
		// System.out.println("mission complete");
		// t1 = null;
		// System.gc();
		// System.out.println("first gc complete");

		t1 = new ThreadLocal<SimpleDateFormat>();
		cd = new CountDownLatch(num);
		for (int i=0; i<num; i++) {
			es.execute(new ParseDate(i));
		}
		cd.await();
		Thread.sleep(1000);
		System.gc();
		System.out.println("second gc complete");
	}

}