package Cons_DAG;

import java.util.ArrayList;
import java.util.Comparator;

public class Rule implements Comparable<Rule> {
	
	private String source_ip; 
	private String des_ip;
	private int priority;
	private int weight;
	private int source_mask;
	private int des_mask;
	
	// ArrayList<String> list = new ArrayList<String>();
	
	public Rule(String source_ip, String des_ip, int source_mask, int des_mask, int priority, int weight) {
		
		
		this.source_ip = source_ip;
		this.des_ip = des_ip;
		this.priority = priority;
		this.weight = weight;
		this.source_mask = source_mask;
		this.des_mask = des_mask;

	}
	public String getSource() {
		
		return source_ip;
		
		
	}
	// Find all possible match for each rule in the field 'match'.
	
	
	public String getDes() {
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
	public int getSourceMask() {
			
		return source_mask;
	}
	public int getDesMask() {
		
		return des_mask;
	}

	@Override
	public int compareTo(Rule r) {
		int pro = ((Rule) r).getPriority();
		return pro- this.priority;
	}
	




}
