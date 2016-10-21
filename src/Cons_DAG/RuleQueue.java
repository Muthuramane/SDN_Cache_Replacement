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
	
	public void add_If_Miss(Rule r) {
		cache.addFirst(r);
		count++;
	}
	
	public void add_If_Hitted(Rule r) {
		cache.remove(r);
		cache.addFirst(r);
		count++;
	}
	
	public void deleteAll(ArrayList<Rule> list) {
		cache.removeAll(list);
	}
	
	public void delete(Rule rule) {
		cache.remove(rule);
	}
	
	public Rule first() {
		int i = 0;
		while (cache.get(i).judge() == false) {
			i++;
		}
		System.out.println("i is "+i);
		return cache.get(i);
	}
	
	public  boolean contain (Rule r) {
		if (cache.contains(r)) {
			return true;
		}
		return false;
	}
	
	public Rule last() {
		int i = cache.size()-1;
		while (cache.get(i).judge() == false) {
			i--;
		}
		//System.out.println("i is "+i);
		return cache.get(i);
	}
	
	public LinkedList<Rule> getCache() {
		return cache;
	}


	
	

}
