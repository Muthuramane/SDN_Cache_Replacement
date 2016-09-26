package Cons_DAG;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Construct {
	
	// Get the rule dependency stored in the Map with the key of each rule.
	
	
	static Map<Rule, ArrayList<Rule>> deps = new HashMap<Rule, ArrayList<Rule>> () ;
	
	Map<Rule, ArrayList<Rule>> rule_set = new HashMap<Rule, ArrayList<Rule>> () ;
	
	HashSet<Rule> result_set = new HashSet<Rule> () ;
	
	Map<ArrayList<String>, Integer> trace = new HashMap<ArrayList<String>, Integer>();
	
	static int total_trace = 0;
	
	public Construct() {
			
		ArrayList<Rule> Rules = new ArrayList<Rule>();
		// createTxtFile 作用是读取源数据，将ip掩码小于18的数据筛掉
		//createTxtFile("./data_set/MyFilters1k"); rule4000_trace MyFilters_acl2_10k_trace
		// readTxtfile 作用是将生成的数据加入ArrayList<Rule> 中
		readTraceFile("./data_set/rule4000_trace", trace);
		
		readTxtFile("./data_set/rule4000", Rules, trace);
		
		// 如果一个规则没有依赖的rules，就加入一个空ArrayList
		for (int i = 0; i < Rules.size(); i++) {
			
		
			if (deps.get(Rules.get(i))  == null) {
				deps.put(Rules.get(i), new ArrayList<Rule> ());
			}
			
		}
		
		// 此处循环，将所有priority 大于Ri的所有rules加入到potentialParent中，并用function addParents(Rules.get(i), potentialParents ) 来寻找依赖关系
		for (int i = Rules.size()-1; i >=0 ; i--) {
			
			ArrayList<Rule> potentialParents = new ArrayList<Rule>();
			// System.out.println("test 1");
			Collections.sort(Rules);
			
			for (int j = 0; j < Rules.size(); j++) {
				
				if (Rules.get(i).getPriority() >= Rules.get(j).getPriority() && i != j) {
					
					potentialParents.add(Rules.get(j));
					
				}
				
			}
			//System.out.println(Rules.get(i).getNumber());
			addParents(Rules.get(i), potentialParents );
			
		}
		

		
		/*
		for (int i = 0; i < Rules.size(); i++) {
			if (deps.get(Rules.get(i)) != null) {
				ArrayList<Rule> print = new ArrayList<Rule> (deps.get(Rules.get(i)));
			
				for (int j = 0; j < print.size(); j++) {
					System.out.println(Rules.get(i).getNumber() + " <- "+print.get(j).getNumber());
				}
			}
			
		}
		
		for (int i = 0; i < Rules.size(); i++) {

				ArrayList<Rule> temp_deps = new ArrayList<Rule> (deps.get(Rules.get(i)));
			
				System.out.print("The rule number is "+Rules.get(i).getNumber()+" and the level is "+Rules.get(i).getLevel()+ " and need cache the related rule ");
			
			
				for (int j = 0; j < temp_deps.size(); j++) {
						
						System.out.print(temp_deps.get(j).getNumber()+" ");
				
				}
			 
			System.out.println();
			
		}
		*/
		// 此处结束了依赖关系的搭建.
		// Input the un-cached rules with the number of available entries in TCAM.
		/**
		Rule input = Rules.get(1);
		int size = 10;
		float test = 0;//calculate(input, size);
		System.out.print("If we cache Rule"+input.getNumber()+" and the size of TCAM is "+size+", the algorithm would select ");
		//Print the result stored in rule_set.
		ArrayList<Rule> print = new ArrayList<Rule>(rule_set.get(input));
		for (int i = 0; i < print.size(); i++) {
			System.out.print("Rule"+print.get(i).getNumber()+" ");
		}
		System.out.println("to cache. The CACM is "+test);
			
		Collections.sort(Rules, new Comparator<Rule>() {

			public int compare(Rule o1, Rule o2) {
				
				return o2.getWeight()-o1.getWeight();
			}
			
		});
		//int size = 5;
		// wildcard_rules_algo (size, Rules);
		for (Rule r: result_set) {
			System.out.print("Rule"+r.getNumber()+" ");
		}
		*/
		
		
		// Start assigning size and calculate the ratio
		double nvm = 0.208;
		double sram = 0.1;
		int size = 1000;
		int nvm_size = (int) (size*nvm);
		int sram_size = (int) (size*sram);
		
		for (int i = 0; i < Rules.size(); i++) {
			total_trace = total_trace + Rules.get(i).getWeight();
		}
		
		for (int i = 0; i < 11; i++) {
			result_set = new HashSet<Rule>();
			int current_size = i*nvm_size + (10-i)*sram_size;
			//System.out.println("Before "+result_set.size());
			// System.out.println("Rule number is "+Rules.size());
			System.out.println("TCAM size is "+current_size);
			
			independent_set_algo (current_size, Rules);
			System.out.println("Cache "+result_set.size()+" rules");
			//System.out.print("We need to cache ");
			ArrayList<Rule> print = new ArrayList<Rule>(result_set);
			// System.out.println("After"+print.size());
			int hit_trace = 0;
			for (int j = 0; j < print.size(); j++) {
				//System.out.print("Rule"+print.get(j).getNumber()+" ");
				hit_trace = hit_trace + print.get(j).getWeight();
			}
			// System.out.println("");
			float hit_ratio = ((float) hit_trace) / ((float) total_trace);
			System.out.println("Tatol trace is "+total_trace+" and Total number of rules is "+Rules.size());
			System.out.println("The hit couts is "+hit_trace+" and the hit ratio is "+hit_ratio*100+"%");
			System.out.println();
			
		}

		
		
	}
	
	private void independent_set_algo (int size, ArrayList<Rule> list) {
		
		Collections.sort(list, new Comparator<Rule>() {

			public int compare(Rule o1, Rule o2) {
				
				ArrayList<Rule> duplicate_o2 = new ArrayList<Rule>();
				// ArrayList<Rule> current_result_set = new ArrayList<Rule>(result_set);
				duplicate_o2 = deps.get(o2);
				duplicate_o2.retainAll(result_set);
				
				ArrayList<Rule> duplicate_o1 = new ArrayList<Rule>();
				// ArrayList<Rule> current_result_set = new ArrayList<Rule>(result_set);
				duplicate_o1 = deps.get(o1);
				duplicate_o1.retainAll(result_set);
				
				double o2_value = ((double) o2.getWeight()) / ((double) 1+deps.get(o2).size()-duplicate_o2.size());
				double o1_value = ((double) o1.getWeight()) / ((double) 1+deps.get(o1).size()-duplicate_o1.size());
				
				if (o2_value > o1_value) {
					return 1;
				} else if (o2_value < o1_value) {
					return -1;
				}
				return 0;
			}
			
		});
		
		for (int i = 0; i < list.size(); i++) {
			
			Rule rule = list.get(i);
			//System.out.println("Check Rule"+rule.getNumber());
			if (!result_set.contains(rule)){
				
				HashSet<Rule> temp_set = new HashSet<Rule>(result_set);
				temp_set.add(rule);
				temp_set.addAll(deps.get(rule));
				
				if (temp_set.size() <= size) {
					
					//System.out.println("Add?");
					//System.out.println("Rule"+rule.getNumber()+" size is "+temp_set.size());
					result_set.add(rule);
					result_set.addAll(deps.get(rule));
				} 
				if (result_set.size() == size) {
					break;	
				}
			}
			

		}
	}
	
	private void wildcard_rules_algo(int size, ArrayList<Rule> list) {
		
		
		for (Rule r: list) {
			System.out.print("Rule"+r.getNumber()+" ");
		}
		System.out.println();
		outer:
		for (int i = 0; i < list.size(); i++) {
			
			if (result_set.containsAll(deps.get(list.get(i))) && size > 0) {
				result_set.add(list.get(i));
				size = size -1;
			}
			
			float test = calculate(list.get(i), size);
			//System.out.println("i is "+i+" the test is "+test);
			if (test > 0) {
				
				if (result_set.contains(list.get(i))) {
					//System.out.println("i is "+i+" the first if");
					// continue;
				} else {
					size = size - (deps.get(list.get(i))).size()-1;
					//System.out.println("i is "+i+" the first else");
					// result_set.add(list.get(i));
					result_set.addAll(rule_set.get(list.get(i)));
					// break;
				}
			} else if (size <= 0) {
				break outer;
			} 
			System.out.println("The "+i+" round: size is "+size);
		}
		
		
	}

	// Algorithm implemented by the function calculate.
	public  float calculate(Rule rule, int size_TCAM) {
		
		int cost = 0;
		float CMACV;
		float ACV = 0;
		ArrayList<Rule> list = new ArrayList<Rule>();
		ArrayList<Rule> ruleSet = new ArrayList<Rule>();

		list = (ArrayList<Rule>) deps.get(rule).clone();
		cost = list.size()+1;

		if (cost <= size_TCAM) {
			
			CMACV = (rule.getWeight())/cost;
			ArrayList<Rule> init = new ArrayList<Rule> ();
			init.add(rule);
			rule_set.put(rule, init);
			ruleSet.add(rule);
			
		} else {
			
			return 0;
		}
		
		boolean flag = true;
		
		while (flag) {

			int weight = 0;
			float max_con = 0;
			Rule max_rule = rule;
			float contribution;
			
			for (int i = 0; i < list.size(); i++) {
				ArrayList<Rule> parents = new ArrayList<Rule>();
				Rule temp = list.get(i);
				parents = (ArrayList<Rule>) deps.get(temp).clone();
				
				parents.retainAll(ruleSet);
				
				
				if (parents.size() > 0) {
					contribution = temp.getWeight()/(parents.size());
				} else {
					contribution =  temp.getWeight();
				}
				if (max_con < contribution) {
					max_con = contribution;
					max_rule = temp;
				}
				
			}

			ArrayList<Rule> combine = new ArrayList<Rule> (ruleSet);
			for (int i = 0; i < ruleSet.size(); i++) {
				combine.addAll(deps.get(ruleSet.get(i)));
				weight = ruleSet.get(i).getWeight()+weight;
			}
			Set<Rule> set  = new HashSet<Rule>(combine);
			ArrayList<Rule> all = new ArrayList<Rule>();
			all.addAll(set);
			
			ArrayList<Rule> next = new ArrayList<Rule>();
			next = (ArrayList<Rule>) deps.get(max_rule).clone();
			
			ArrayList<Rule> temp_list = new ArrayList<Rule>();		
			temp_list = (ArrayList<Rule>) next.clone();
			temp_list.retainAll(all);
			next.removeAll(temp_list);
			
			int combineCost = next.size();
			
			if (max_rule == rule) {

				flag = false;
				
			}else if (combineCost+all.size() <= size_TCAM) {
				
				ACV = (weight+max_rule.getWeight())/(combineCost+all.size());
				
				ruleSet.add(max_rule);
				
				list.remove(max_rule);

				list.removeAll(deps.get(max_rule));

				list.addAll(deps.get(max_rule));
				
				
			} else{
				
				flag = false;
			}
			
			if (ACV > CMACV) {
				CMACV = ACV;
				rule_set.put(rule, ruleSet);
			}
			
			
		}
		
		return CMACV;
	}

	// Function to construct the rule dependency DAG.
	public Map<Rule, ArrayList<Rule>> addParents(Rule rule, ArrayList<Rule> parent) {
				
		Collections.sort(parent);
		//System.out.println("Start Rule"+rule.getNumber());
		for (int i = 0; i < parent.size(); i++) {
			
			Rule rj = parent.get(i);		
			//System.out.println("Rule"+rj.getNumber());
			Long source_ip_r1 = Long.valueOf(rule.getSource());
			Long des_ip_r1 = Long.valueOf(rule.getDes());
			Long source_ip_r2 = Long.valueOf(rj.getSource());
			Long des_ip_r2 = Long.valueOf(rj.getDes());
			
			int source_mask_r1 = rule.getSourceMask();
			int des_mask_r1 = rule.getDesMask();
			int source_mask_r2 = rj.getSourceMask();
			int des_mask_r2 = rj.getDesMask();
			
			// System.out.println(rule.getNumber()+"  "+source_ip_r1 +"    and " +rj.getNumber()+"   "+ source_ip_r2);
			
			if ( match (source_ip_r1, source_ip_r2, source_mask_r1, source_mask_r2) && 
				 match (des_ip_r1, des_ip_r2, des_mask_r1, des_mask_r2) &&
				 !deps.get(rule).contains(rj)) 
			//	 && (rule.getLevel()-rj.getLevel() <= 1 || rule.getLevel() == 0)) 
			{
				//int level = rj.getLevel()+1;
				//if (level != rule.getLevel()) {
					// rule.fitLevel(rj.getLevel());
				// }
				HashSet<Rule> dep_temp = new HashSet<Rule>();
				if (deps.containsKey(rule)) {
								
					dep_temp = new HashSet<Rule>(deps.get(rule));
						
				} 	
				HashSet<Rule> rj_children = new HashSet<Rule>(deps.get(rj));
				dep_temp.add(rj);
				dep_temp.addAll(rj_children);
				ArrayList<Rule> rule_children = new ArrayList<Rule> (dep_temp);
				deps.put(rule, rule_children);

			} 
			
			
		} 
		Collections.sort(deps.get(rule));
		/*
		for (int i = 0; i < parent.size(); i++) {
			if (parent.get(i).getLevel()+1 == rule.getLevel() && !deps.get(rule).contains(parent.get(i))) {
				parent.get(i).increaseLevel();
			}
		}
	*/
		return deps;
		
	}
	
	public boolean match (Long ip1, Long ip2, int mask1, int mask2) {
		
		int mask_short;
		//int ip1_int = ip1;
		
		if (mask1 > mask2) {
			mask_short = mask2;
		} else {
			mask_short = mask1;
		}
		///System.out.println(ip1+"  "+ip2);
		//System.out.println(mask_short);
		// System.out.println((ip1 >> (32-mask_short)) +"    "+(ip2 >> (32-mask_short)));
		return ((ip1 >> (32-mask_short)) == (ip2>> (32-mask_short)));
	}
	
	/**

     * 功能：Java读取txt文件的内容

     * 步骤：1：先获得文件句柄

     * 2：获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取

     * 3：读取到输入流后，需要读取生成字节流

     * 4：一行一行的输出。readline()。

     * 备注：需要考虑的是异常情况

     * @param filePath
	 * @param trace TODO

     */

    public static void readTxtFile(String filePath, ArrayList<Rule> list, Map<ArrayList<String>, Integer> trace){

        try {

                String encoding="GBK";

                File file=new File(filePath);

                if(file.isFile() && file.exists()){ //判断文件是否存在

                    InputStreamReader read = new InputStreamReader(

                    new FileInputStream(file),encoding);//考虑到编码格式

                    BufferedReader bufferedReader = new BufferedReader(read);

                    String lineTxt = null;
                    int i = 0; 
                    HashSet<String> remove_duplicate = new HashSet<String>();
                    while((lineTxt = bufferedReader.readLine()) != null){
                    	
                        String[] temp = lineTxt.split("\n");

                    	String[] temp_for = temp[0].split("\t");
                    	String temp_source = temp_for[0].split("@")[1];
                    	String[] source = temp_source.split("/");
                    	String[] target = temp_for[1].split("/");
                    	// temp_source[0];
                    	
                    	int source_mask = Integer.valueOf(source[1]);
                    	int des_mask = Integer.valueOf(target[1]);

                    	String source_ip = ip2int(source[0]);
                    	String des_ip = ip2int(target[0]);
                    	
                    	//System.out.println(source_ip+"\t"+des_ip);
                    	// System.out.println("des ip is "+des_ip);
                    	String combine = source_ip+des_ip;
                    	if (remove_duplicate.contains(combine)) {
                    		continue;
                    	}
                    	remove_duplicate.add(combine);
                    	ArrayList<String> ip_s_t = new ArrayList<String>();
                    	ip_s_t.add(source_ip);
                    	ip_s_t.add(des_ip);
                    	int weight;
                    	if (trace.containsKey(ip_s_t)) {
                    		weight = trace.get(ip_s_t);
                    	} else {
                    		weight = 0;
                    	}
                    	
                    	
                    	//System.out.println("Rule"+(i+1)+" Weight is "+weight);
                    	Rule r = new Rule(source_ip, des_ip, source_mask, des_mask, i, weight);

                    	list.add(r);
                    	i = i+ 1;
                    }
                        
                    read.close();

        }else{
            System.out.println("找不到指定的文件");
        }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
    }
    
    public static void readTraceFile(String filePath, Map<ArrayList<String>, Integer> trace){

        try {

                String encoding="GBK";

                File file=new File(filePath);

                if(file.isFile() && file.exists()){ //判断文件是否存在

                    InputStreamReader read = new InputStreamReader(

                    new FileInputStream(file),encoding);//考虑到编码格式

                    BufferedReader bufferedReader = new BufferedReader(read);

                    String lineTxt = null;
                    int i = 0;
                    while((lineTxt = bufferedReader.readLine()) != null){
                    	
                    	ArrayList<String> ip_source_des = new ArrayList<String>();
                    	
                        String[] temp = lineTxt.split("\n");
                        // Each component
                    	String[] temp_for = temp[0].split("\t");
                    	
                    	String source = temp_for[0];
                    	String target = temp_for[1];
                    	System.out.println(i);
                    	i++;
                    	ip_source_des.add(source);
                    	ip_source_des.add(target);
                    	int weight = 0;
                    	if (!trace.containsKey(ip_source_des)) {
                    		weight = 1;
                    		trace.put(ip_source_des, weight);
                    	} else {
                    		weight = trace.get(ip_source_des)+1;
                    		trace.put(ip_source_des, weight);
                    	}
                    	
                    	// System.out.println("source ip is "+source+" and des ip is "+target);
                    	
                    	// total_trace++;
                    }
                    
                    
                        
                    read.close();

        }else{
            System.out.println("找不到指定的文件");
        }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
    }
    
    public static String ip2int(String ip){ 
    	String[] items = ip.split("\\.");
    	
    	Long num = Long.valueOf(items[0])<<24 
    	    	|Long.valueOf(items[1])<<16 
    	    	|Long.valueOf(items[2])<<8 
    	    	|Long.valueOf(items[3]); 
    	String binary = Long.toString(num);
    	
    	return binary; 
    } 

    
	public static void main(String[] args) {
		new Construct();
		
		System.out.println("The end"); 
		
	}

}
