package Cons_DAG;

public class Pair {
	
	private Long min;
	private Long max;
	
	public Pair (Long min, Long max) {
		this.min = min;
		this.max = max;
	}
	
	public Long getMin () {
		return min;
	}
	
	public Long getMax () {
		return max;
	}
	
	public void changeMax (Long i) {
		max = i;
	}
	
	public void changeMin (Long i) {
		min = i;
	}
}
