public class c2_3 {
	private volatile static boolean ready;
	private volatile static int number;
	private static class ReadThread extends Thread {
		public void run() {
			while (!ready) {
				System.out.println(number);
			}
			System.out.println(number);
		}
	}
	public static void main(String[] args) throws InterruptedException {
		new ReadThread().start();
		Thread.sleep(1000);
		number = 123;
		ready = true;
		Thread.sleep(10000);
	}
}