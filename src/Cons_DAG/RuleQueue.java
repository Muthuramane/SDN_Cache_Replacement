package Cons_DAG;

import java.util.LinkedList;

public class RuleQueue {
	
	 private LinkedList<Rule> linkedList = new LinkedList<Rule>();  
	  private int count = 0;
	      
	    public void add(Rule coming){  
	        linkedList.addFirst(coming);  
	        count++;
	    }  
	   
	    public Rule getNewest(){  
	        return linkedList.getFirst();  
	    }  
	    
	    public Rule getOldest(){  
		return linkedList.getLast();  
	    }  
	   
	    public Rule delete(){  
	        return linkedList.removeLast();  
	    }  
	   
	    public int size(){  
	        return linkedList.size();  
	    }  
	    
	    public int getCount(){  
		        return count;  
	    }
	      
	     
	    public boolean isEmpty(){  
	        return linkedList.isEmpty();  
	    }  

}
