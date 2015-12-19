import java.util.*;
import java.io.*;

public class c2_2 implements Runnable
{
	public static void main(String[] args)
	{
		Thread t1 = new Thread(new c2_2());
		t1.start();
	}

	//@override
	public void run() {
		System.out.println("lalalala~~~~");
	}
}