package Cons_DAG;

import java.util.ArrayList;
import java.util.Comparator;

public class Rule implements Comparable<Rule> {
	
	private ArrayList<String> source_ip; 
	private ArrayList<String> des_ip;
	private int priority;
	private int weight;
	
	// ArrayList<String> list = new ArrayList<String>();
	
	public Rule(ArrayList<String> source_ip, ArrayList<String> des_ip, int priority, int weight) {
		
		
		this.source_ip = source_ip;
		this.des_ip = des_ip;
		this.priority = priority;
		this.weight = weight;

	}
	public ArrayList<String> getSource() {
		
		return source_ip;
		
		
	}
	// Find all possible match for each rule in the field 'match'.
	
	
	public ArrayList<String> getDes() {
		return des_ip;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public int getWeight() {
		return weight;
	}
	
	public int getNumber() {
		
		return priority;
	}

	@Override
	public int compareTo(Rule r) {
		int pro = ((Rule) r).getPriority();
		return pro- this.priority;
	}
	




}
