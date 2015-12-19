public class c2_2_3 {

	public static void main(String[] args) throws InterruptedException {
		Thread t1 = new Thread() {
			public void run() {
				while (true) {
					if (Thread.currentThread().isInterrupted()) {
						System.out.println("normal exit");
						break;
					}
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						System.out.println("interrupted when sleep");
						Thread.currentThread().interrupt();
					}
					Thread.yield();
				}
			}
		};
		t1.start();
		Thread.sleep(2000);
		t1.interrupt();
	}

}