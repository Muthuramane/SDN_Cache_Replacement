package Cons_DAG;

public class PairVS {
	
	private Double value;
	private Integer size;
	
	public PairVS (double value, int size) {
		this.value = value;
		this.size = size;
	}
	
	public double getValue () {
		return value;
	}
	
	public int getSize () {
		return size;
	}
	
	public void changeSize (int i) {
		size = i;
	}
	
	public void changeValue (double i) {
		value = i;
	}
}
