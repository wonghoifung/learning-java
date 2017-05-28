import java.util.*;

public class ch2
{
	interface Predicate<T> {
		boolean test(T t);
	}
	public static <T> List<T> filter2(List<T> list, Predicate<T> p) {
		List<T> result = new ArrayList<>();
		for (T e:list) {
			if (p.test(e)) result.add(e);
		}
		return result;
	}

	public static void main(String[] args)
	{
		List<Apple> inventory = Arrays.asList(new Apple(80,"green"), new Apple(155, "green"), new Apple(120, "red"));

		List<Apple> greenApples2 = filter(inventory, new AppleColorPredicate());
		System.out.println(greenApples2);

		List<Apple> redApples2 = filter(inventory, (Apple apple)->"red".equals(apple.getColor()));
		System.out.println(redApples2);

		List<Apple> heavyApples2 = filter2(inventory, (Apple apple)->apple.getWeight()>150);
		System.out.println(heavyApples2);
	}
	public static List<Apple> filter(List<Apple> inventory, ApplePredicate p){
		List<Apple> result = new ArrayList<>();
		for(Apple apple : inventory){
			if(p.test(apple)){
				result.add(apple);
			}
		}
		return result;
	} 
	public static class Apple {
		private int weight = 0;
		private String color = "";

		public Apple(int weight, String color){
			this.weight = weight;
			this.color = color;
		}

		public Integer getWeight() {
			return weight;
		}

		public void setWeight(Integer weight) {
			this.weight = weight;
		}

		public String getColor() {
			return color;
		}

		public void setColor(String color) {
			this.color = color;
		}

		public String toString() {
			return "Apple{" +
					"color='" + color + '\'' +
					", weight=" + weight +
					'}';
		}
	}
	interface ApplePredicate {
		public boolean test(Apple a);
	}
	static class AppleWeightPredicate implements ApplePredicate{
		public boolean test(Apple apple){
			return apple.getWeight() > 150; 
		}
	}
	static class AppleColorPredicate implements ApplePredicate{
		public boolean test(Apple apple){
			return "green".equals(apple.getColor());
		}
	}
	static class AppleRedAndHeavyPredicate implements ApplePredicate{
		public boolean test(Apple apple){
			return "red".equals(apple.getColor()) 
					&& apple.getWeight() > 150; 
		}
	}
}