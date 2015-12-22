public class c2_6 {
	public static class HP extends Thread {
		static int count = 0;
		public void run() {
			while (true) {
				synchronized (HP.class) {
					count++;
					if (count > 100000000) {
						System.out.println("HP is complete");
						break;
					}
				}
			}
		}
	}

	public static class LP extends Thread {
		static int count = 0;
		public void run() {
			while (true) {
				synchronized (LP.class) {
					count++;
					if (count > 100000000) {
						System.out.println("LP is complete");
						break;
					}
				}
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		HP h = new HP();
		LP l = new LP();
		h.setPriority(Thread.MAX_PRIORITY);
		l.setPriority(Thread.MIN_PRIORITY);
		l.start();
		h.start();
	}
}