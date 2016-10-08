package Cons_DAG;

import java.util.ArrayList;
import java.util.LinkedList;

public class RuleQueue{
	
	
	private LinkedList<Rule> cache;
	private int count = 0;
	
	public RuleQueue (LinkedList<Rule> cache, int count){
		this.cache = cache;
		this.count = count;		
	}
	
	public void add(Rule r) {
		cache.addFirst(r);
		count++;
	}
	
	public void delete(ArrayList<Rule> list) {
		cache.removeAll(list);
	}
	
	public Rule first() {
		return cache.getFirst();
	}
	
	public Rule last() {
		return cache.getLast();
	}


	
	

}
