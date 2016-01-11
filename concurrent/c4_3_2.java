import java.util.concurrent.*;
import java.util.*;

public c4_3_2 {

	static volatile ThreadLocal<SimpleDateFormat> t1 = new ThreadLocal<SimpleDateFormat>() {
		protected void finalize() throws Throwable {
			System.out.println(this.toString() + " is gc");
		}
	};

	static volatile CountDownLatch cd = new CountDownLatch(10000);

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
							System.out.println(this.toString() + " is gc");
						}
					});
					System.out.println(Thread.currentThread().getId() + ":create SimpleDateFormat");
				}
				Date t = t1.get().parse("2016-01-11 8:39:" + i % 60);
			} catch (ParseException e) {
				e.printStackTrace();
			} finally {
				cd.countDown();
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		// TODO
	}

}