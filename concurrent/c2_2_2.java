
public class c2_2_2 {

	public static User u = new User();

	public static class User {
		private int id;
		private String name;
		public int getId() { return id; }
		public void setId(int d) { id = d; }
		public String getName() { return name; }
		public void setName(String n) { name = n; }
		public User() {
			id = 0;
			name = "0";
		}
		public String toString() {
			return "User [id=" + id + ", name=" + name + "]";
		}
	}

	public static class WriteThread extends Thread {
		volatile boolean stop = false;
		public void stopMe() {
			stop = true;
		}
		public void run() {
			while (true) {
				if (stop) {
					System.out.println("normal exit");
					break;
				}
				synchronized(u) {
					int v = (int)(System.currentTimeMillis() / 1000);
					u.setId(v);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					u.setName(String.valueOf(v));
				}
				Thread.yield();
			}
		}
	}

	public static class ReadThread extends Thread {
		public void run() {
			while (true) {
				synchronized(u) {
					if (u.getId() != Integer.parseInt(u.getName())) {
						System.out.println(u.toString());
					}
				}
				Thread.yield();
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		new ReadThread().start();
		while (true) {
			Thread t = new WriteThread();
			t.start();
			Thread.sleep(150);
			//t.stop();
			((WriteThread)t).stopMe();
		}
	}

}