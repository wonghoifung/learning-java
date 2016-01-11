import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.*;
import java.text.*;

public class c4_4_5 {

	static AtomicStampedReference<Integer> money = new AtomicStampedReference<Integer>(19,0);

	public static void main(String[] args) {
		for (int i=0; i<3; ++i) {
			final int timestamp = money.getStamp();
			new Thread() {
				public void run() {
					while (true) {
						while (true) {
							Integer m = money.getReference();
							if (m < 20) {
								if (money.compareAndSet(m, m+20, timestamp, timestamp+1)) {
									System.out.println("balance less than 20, recharge, "+money.getReference());
									break;
								}
							} else {
								break;
							}
						}
					}
				}
			}.start();
		}

		new Thread() {
			public void run() {
				for (int i=0; i<100; ++i) {
					while (true) {
						int timestamp = money.getStamp();
						Integer m = money.getReference();
						if (m > 10) {
							System.out.println("more than 10");
							if (money.compareAndSet(m, m-10, timestamp, timestamp+1)) {
								System.out.println("consume 10, left:"+money.getReference());
								break;
							}
						} else {
							System.out.println("not enough balance");
							break;
						}
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {}
				}
			}
		}.start();

	}

}