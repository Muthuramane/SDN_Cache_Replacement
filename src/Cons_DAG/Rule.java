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
	private int level;
	private Pair source_range;
	private Pair des_range;
	private String cover_rule;
	
	// ArrayList<String> list = new ArrayList<String>();
	
	public Rule(String source_ip, String des_ip, Pair source_range, Pair des_range,  int priority, int weight, int source_mask, int des_mask) {
		
		
		this.source_ip = source_ip;
		this.des_ip = des_ip;
		this.priority = priority;
		this.weight = weight;
		this.source_mask = source_mask;
		this.des_mask = des_mask;
		this.source_range = source_range;
		this.des_range = des_range;
		// this.level = level;

	}
	
	public Rule (String cover_rule, int priority) {
		this.cover_rule = cover_rule;
		this.priority = priority;
		//this.priority = priority;
	}
	
	public boolean judge () {
		if (cover_rule == null) {
			// Normal rule
			return true;
		} else {
			// Cover rule
			return false;
		}
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
		
		return priority+1;
	}
	public int getSourceMask() {
			
		return source_mask;
	}
	public int getDesMask() {
		
		return des_mask;
	}
	
	public Pair getSourceRange() {
		
		return source_range;
		
		
	}
	
	public Pair getDesRange() {
		
		return des_range;
		
		
	}
	
	public int getLevel() {
		return level;
	}
	public void fitLevel(int n) {
		level = n+1;
	}
	public void increaseLevel() {
		level++;
	}
	public void decreaseLevel() {
		level--;
	}

	@Override
	public int compareTo(Rule r) {
		int pro = ((Rule) r).getPriority();
		return pro- this.priority;
	}
	
	public String toString() {
		if (cover_rule != null) {
			return cover_rule;
		} else {
			return "Rule"+getNumber();
		}
	}



}
