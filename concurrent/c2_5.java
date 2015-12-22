public class c2_5 {
	public static class DT extends Thread {
		public void run() {
			while (true) {
				System.out.println("alive");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public static void main(String[] args) throws InterruptedException {
		Thread t = new DT();
		t.setDaemon(true);
		t.start();
		Thread.sleep(2000);
	}
}