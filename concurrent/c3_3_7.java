import java.util.concurrent.*;
import java.util.*;

public class c3_3_7 {

	public static void main(String[] args) {
		Map<Integer,Integer> map = new ConcurrentSkipListMap<Integer,Integer>();
		for (int i=0; i<30; ++i) {
			map.put(i,i+100);
		}
		for (Map.Entry<Integer,Integer> entry:map.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}
	}

}