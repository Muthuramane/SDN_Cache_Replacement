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
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

public class Construct {

	// direct dependency, store all direct children rules
	static Map<Rule, ArrayList<Rule>> deps_child = new HashMap<Rule, ArrayList<Rule>> () ;
	
	// direct + indirect dependency, store all children rules
	static Map<Rule, ArrayList<Rule>> deps_child_all = new HashMap<Rule, ArrayList<Rule>> () ;
	
	// store the father rules
	static Map<Rule, ArrayList<Rule>> deps_father = new HashMap<Rule, ArrayList<Rule>> () ;
	
	// direct + indirect dependency, store all father rules
	static Map<Rule, ArrayList<Rule>> deps_father_all = new HashMap<Rule, ArrayList<Rule>> () ;
	
	static Map<String, Rule> relation_Rule = new HashMap<String, Rule> () ;
	
	// Store the cost and value for each rule, updated with each iteration
	Map<Rule, PairVS> cover_value = new HashMap<Rule, PairVS>();
	Map<Rule, PairVS> independent_value = new HashMap<Rule, PairVS>();
	
	
	Map<Rule, ArrayList<Rule>> rule_set = new HashMap<Rule, ArrayList<Rule>> () ;

	//RuleQueue cache = new RuleQueue();
	
	
	// Store the rules cached in TCAM
	static LinkedHashSet<Rule> result_set = new LinkedHashSet<Rule> () ;

	// Store each rule's weight, get from the trace file
	static Map<ArrayList<String>, Integer> trace = new HashMap<ArrayList<String>, Integer>();

	static int total_trace = 0;
	
	static ArrayList<Rule> Rules = new ArrayList<Rule>();
	
	static ArrayList<Rule> input_Rules = new ArrayList<Rule>();
	
	public Construct() {

		
		
		//readTxtFile("./data_set/MyFilters1k"); rule4000_trace MyFilters_acl2_10k_trace
		// Read trace file, get weight for each possible rule.
		readTraceFile("./data_set/MyFilter9_trace", trace);
		
		// Grasp the rules in input file, assign corresponding weight.
		readTxtFile("./data_set/MyFilter9", Rules, trace);
		
		readInputTrace("./data_set/MyFilter9_input");

		// Initialization 
		for (int i = 0; i < Rules.size(); i++) {
			deps_child.put(Rules.get(i), new ArrayList<Rule> ());
			deps_child_all.put(Rules.get(i), new ArrayList<Rule> ());
			deps_father.put(Rules.get(i), new ArrayList<Rule> ());
			deps_father_all.put(Rules.get(i), new ArrayList<Rule> ());
		}

		/* 
		 *  1. For each rule,  add all rules with higher priority as a potential candidate list.  
		 *  2. Apply function addParents to find dependency.
		 */
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




		for (int i = 0; i < Rules.size(); i++) {
			if (deps_child.get(Rules.get(i)) != null) {
				ArrayList<Rule> print = new ArrayList<Rule> (deps_child.get(Rules.get(i)));

				for (int j = 0; j < print.size(); j++) {
					System.out.println(Rules.get(i).getNumber() + " <- "+print.get(j).getNumber());
				}
			}

		}

		for (int i = 0; i < Rules.size(); i++) {

			ArrayList<Rule> temp_deps = new ArrayList<Rule> (deps_child.get(Rules.get(i)));

			System.out.print("The rule number is "+Rules.get(i).getNumber()+" and direct childern rules are ");


			for (int j = 0; j < temp_deps.size(); j++) {

				System.out.print(temp_deps.get(j).getNumber()+" ");

			}
			System.out.println();
			ArrayList<Rule> temp_deps_all = new ArrayList<Rule> (deps_child_all.get(Rules.get(i)));
			System.out.print(" and all childern rules are ");

			for (int j = 0; j < temp_deps_all.size(); j++) {

				System.out.print(temp_deps_all.get(j).getNumber()+" ");

			}

			System.out.println();

		}


		for (int i = 0; i < Rules.size(); i++) {

			ArrayList<Rule> temp_deps = new ArrayList<Rule> (deps_father.get(Rules.get(i)));

			System.out.print("The rule number is "+Rules.get(i).getNumber()+" and father rules are ");


			for (int j = 0; j < temp_deps.size(); j++) {

				System.out.print(temp_deps.get(j).getNumber()+" ");

			}
			
			ArrayList<Rule> temp_deps_all = new ArrayList<Rule> (deps_father_all.get(Rules.get(i)));
			System.out.print(" and all father rules are ");

			for (int j = 0; j < temp_deps_all.size(); j++) {

				System.out.print(temp_deps_all.get(j).getNumber()+" ");

			}

			System.out.println();


		}

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


		// Assign size to TCAM and calculate hit ratio
		double nvm =  0.208;
		double sram = 0.1;
		int size =6;
		int nvm_size = (int) (size*nvm);
		int sram_size = (int) (size*sram);

		result_set = new LinkedHashSet<Rule>();

		//System.out.println("Before "+result_set.size());
		// System.out.println("Rule number is "+Rules.size());
		System.out.println("TCAM size is "+size);

		ArrayList<Rule> input_Rule = new ArrayList<Rule> (Rules);
		//independent_set_algo (current_size, input_Rule);
		/*
		 *  Apply mix-set algorithm to get the rules that should be cached in TCAM
		 */
		mix_set_algo (size, input_Rule);
		System.out.println("Cache "+result_set.size()+" rules");
		//System.out.print("We need to cache ");
		ArrayList<Rule> print = new ArrayList<Rule>(result_set);
		// System.out.println("After"+print.size());
		int hit_trace = 0;
		for (int j = 0; j < print.size(); j++) {
			System.out.print(print.get(j).toString()+" ");
			hit_trace = hit_trace + print.get(j).getWeight();
		}

		for (int i = 0; i < Rules.size(); i++) {
			total_trace = total_trace + Rules.get(i).getWeight();
		}
		// System.out.println("");
		float hit_ratio = ((float) hit_trace) / ((float) total_trace);
		System.out.println("Tatol trace is "+total_trace+" and Total number of rules is "+Rules.size());
		System.out.println("The hit couts is "+hit_trace+" and the hit ratio is "+hit_ratio*100+"%");
		System.out.println();
		
		for (Rule r: input_Rules) {
			System.out.print(r.toString()+" ");
		}
		System.out.println();
		LRU (6);
		/**
		 * 
		for (int i = 0; i < 11; i++) {
			result_set = new HashSet<Rule>();
			int current_size = i*nvm_size + (10-i)*sram_size;
			//System.out.println("Before "+result_set.size());
			// System.out.println("Rule number is "+Rules.size());
			System.out.println("TCAM size is "+current_size);

			ArrayList<Rule> input_Rule = new ArrayList<Rule> (Rules);
			//independent_set_algo (current_size, input_Rule);
			mix_set_algo (current_size, input_Rule);
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

		**/

	}

	/**
	 * while (size < TCAM_size) {
			sort under cover set 
			sort under independent set
			gasp rule with max value and capable size
				The rule is under cover set algorithm, add(rule), add(cover sets)
				The rule is under independent set algorithm, add(rule), add(direct rule)
		}
	 * @param size
	 * @param list
	 */
	private void mix_set_algo (int size, ArrayList<Rule> list) {
		
		while_loop: while (true) {

			/*
			 *  Sort the list under cover set algorithm
			 */

			Collections.sort(list, new Comparator<Rule>() {

				public int compare(Rule o1, Rule o2) {

					ArrayList<Rule> duplicate_o2 = new ArrayList<Rule>();

					for (int i = 0; i <  deps_child.get(o2).size(); i++) {

						// Only consider the direct dependency and stored corresponding cover set rule.
						Rule cover = new Rule("R"+deps_child.get(o2).get(i).getNumber()+"*", deps_child.get(o2).get(i).getPriority());
						duplicate_o2.add(cover);
					}
					// Get the stored cover set rule to calculate correct size
					duplicate_o2.retainAll(result_set);

					ArrayList<Rule> duplicate_o1 = new ArrayList<Rule>();
					for (int i = 0; i <  deps_child.get(o1).size(); i++) {

						
						Rule cover = new Rule("R"+deps_child.get(o1).get(i).getNumber()+"*", deps_child.get(o1).get(i).getPriority());
						duplicate_o1.add(cover);
					}
					
					duplicate_o1.retainAll(result_set);

					double o2_value = ((double) o2.getWeight()) / ((double) 1+deps_child.get(o2).size()-duplicate_o2.size());
					double o1_value = ((double) o1.getWeight()) / ((double) 1+deps_child.get(o1).size()-duplicate_o1.size());

					PairVS o2_pair = new PairVS(o2_value, 1+deps_child.get(o2).size()-duplicate_o2.size());
					PairVS o1_pair = new PairVS(o1_value, 1+deps_child.get(o1).size()-duplicate_o1.size());
					cover_value.put(o2, o2_pair);
					cover_value.put(o1, o1_pair);

					if (o2_value > o1_value) {						
						return 1;
					} else if (o2_value < o1_value) {						
						return -1;
					} 			
					return 0;
				}

			});
			// Select the capable rule under cover set algorithm
			System.out.println("Size of result set "+result_set.size());
			Rule flag = new  Rule("flag", 0);
			Rule cover_rule = flag;
			select_cover_rule:
				for (int i = 0; i < list.size(); i++) {
					System.out.println("cover "+list.get(i).toString()+" "+cover_value.get(list.get(i)).getValue()+" "+cover_value.get(list.get(i)).getSize());
					if (!result_set.contains(list.get(i)) &&
							cover_value.get(list.get(i)).getSize() +  result_set.size() <= size
							){
						cover_rule = list.get(i);
						break select_cover_rule;
					}
				}


			/*
			 * Sort under independent set algorithm
			 */
			Collections.sort(list, new Comparator<Rule>() {

				public int compare(Rule o1, Rule o2) {

					ArrayList<Rule> duplicate_o2 = new ArrayList<Rule>(deps_child_all.get(o2));
					ArrayList<Rule> copy = new ArrayList<Rule>( duplicate_o2);//duplicate_o2.retainAll(result_set);
					copy.retainAll(result_set);
					ArrayList<Rule> copy2 = new ArrayList<Rule>( duplicate_o2);
					copy2.removeAll(copy);
					/*
					 * The weight here is the sum of the rules with dependency of the selected rule
					 */
					int o2_weight = o2.getWeight();
					for (Rule  r : copy2 ) {
						o2_weight = r.getWeight()+o2_weight;
					}
					
					duplicate_o2.retainAll(result_set);

					ArrayList<Rule> duplicate_o1 = new ArrayList<Rule>(deps_child_all.get(o1));
					copy = new ArrayList<Rule>( duplicate_o1);//duplicate_o2.retainAll(result_set);
					copy.retainAll(result_set);
					copy2 = new ArrayList<Rule>( duplicate_o1);
					copy2.removeAll(copy);
					int o1_weight = o1.getWeight();
					for (Rule  r : copy2 ) {
						o1_weight = r.getWeight()+o1_weight;
					}
					
					duplicate_o1.retainAll(result_set);

					double o2_value = ((double) o2_weight) / ((double) 1+deps_child_all.get(o2).size()-duplicate_o2.size());
					double o1_value = ((double) o1_weight) / ((double) 1+deps_child_all.get(o1).size()-duplicate_o1.size());

					PairVS o2_pair = new PairVS(o2_value, 1+deps_child_all.get(o2).size()-duplicate_o2.size());
					PairVS o1_pair = new PairVS(o1_value, 1+deps_child_all.get(o1).size()-duplicate_o1.size());
					independent_value.put(o2, o2_pair);
					independent_value.put(o1, o1_pair);

					if (o2_value > o1_value) {						
						return 1;
					} else if (o2_value < o1_value) {
						return -1;
					}
					return 0;
				}

			});

			Rule independent_rule = flag;
			select_independent_rule:

				for (int i = 0; i < list.size(); i++) {
					System.out.println("inde "+list.get(i).toString()+" "+independent_value.get(list.get(i)).getValue()+" "+independent_value.get(list.get(i)).getSize() );
					if (!result_set.contains(list.get(i)) &&
							independent_value.get(list.get(i)).getSize() +  result_set.size() <= size
							){
						independent_rule = list.get(i);
						break select_independent_rule;
					}
				}

			//System.out.println(cover_rule.toString()+" "+cover_value.get(cover_rule).getValue());
			//System.out.println(independent_rule.toString()+" "+independent_value.get(independent_rule).getValue());
			System.out.println("in_rule "+independent_rule.toString()+" cover_rule "+cover_rule.toString());
			int decider = 0;

			if (independent_rule.equals(flag) && cover_rule.equals(flag)) {
				break while_loop;
			} else if (independent_rule.equals(flag) && !cover_rule.equals(flag) ){
				decider = 0;
			} else if (!independent_rule.equals(flag) && cover_rule.equals(flag) ) {
				decider = 1;
			} else {
				decider = 2;
			}

			HashSet<Rule> temp_set = new HashSet<Rule>(result_set);

			if (decider == 2){
				if (cover_value.get(cover_rule).getValue() <= independent_value.get(independent_rule).getValue()  ) {
					/*
					 * Add rule under cover set algorithm
					 */
					Rule rule = independent_rule;

					temp_set.add(rule);
					temp_set.addAll(deps_child_all.get(rule));
					System.out.println(rule.toString()+" size is "+temp_set.size());
					if (temp_set.size() < size) {

						//System.out.println("Add?");
						//System.out.println("Rule"+rule.getNumber()+" size is "+temp_set.size());
						result_set.add(rule);
						result_set.addAll(deps_child_all.get(rule));
						list.remove(rule);
						list.removeAll(deps_child_all.get(rule));
						// list.remove(o)

					} else if (temp_set.size() == size) {
						result_set.add(rule);
						result_set.addAll(deps_child_all.get(rule));
						System.out.println("Remain list number is "+list.size());
						break while_loop;	
					} else {
						System.out.println("Remain list number is "+list.size());
						break while_loop;	
					}
				} else {
					/*
					 *  Add rule under independent set algorithm
					 */
					Rule rule = cover_rule;
					HashSet<Rule> temp = new HashSet<Rule>();

					for (int i = 0; i< deps_child.get(rule).size(); i++) {
						Rule add_cover_set = new Rule ("R"+deps_child.get(rule).get(i).getNumber()+"*", deps_child.get(rule).get(i).getPriority());
						temp.add(add_cover_set);
					}	
					temp_set.add(rule);
					temp_set.addAll(temp);
					System.out.println(rule.toString()+" size is "+temp_set.size());
					if (temp_set.size() < size) {

						//System.out.println("Add?");
						//System.out.println("Rule"+rule.getNumber()+" size is "+temp_set.size());
						result_set.add(rule);
						result_set.addAll(temp);
						list.remove(rule);
						//list.removeAll(deps_child_all.get(rule));
						// list.remove(o)

					} else if (temp_set.size() == size) {
						result_set.add(rule);
						result_set.addAll(temp);
						System.out.println("Remain list number is "+list.size());
						break while_loop;	
					} else {
						System.out.println("Remain list number is "+list.size());
						break while_loop;	
					}

				}

			} else if (decider == 0) {
				/*
				 *  Add rule under cover set algorithm
				 */
				Rule rule = cover_rule;
				HashSet<Rule> temp = new HashSet<Rule>();

				for (int i = 0; i< deps_child.get(rule).size(); i++) {
					Rule add_cover_set = new Rule ("R"+deps_child.get(rule).get(i).getNumber()+"*", deps_child.get(rule).get(i).getPriority());
					temp.add(add_cover_set);
				}	
				temp_set.add(rule);
				temp_set.addAll(temp);
				System.out.println(rule.toString()+" size is "+temp_set.size());
				if (temp_set.size() < size) {

					//System.out.println("Add?");
					//System.out.println("Rule"+rule.getNumber()+" size is "+temp_set.size());
					result_set.add(rule);
					result_set.addAll(temp);
					list.remove(rule);
					//list.removeAll(deps_child_all.get(rule));
					// list.remove(o)

				} else if (temp_set.size() == size) {
					result_set.add(rule);
					result_set.addAll(temp);
					System.out.println("Remain list number is "+list.size());
					break while_loop;	
				} else {
					System.out.println("Remain list number is "+list.size());
					break while_loop;	
				}

			} else if (decider == 1) {
				/*
				 *  Add rule under independent set algorithm
				 */
				Rule rule = independent_rule;

				temp_set.add(rule);
				temp_set.addAll(deps_child_all.get(rule));
				System.out.println(rule.toString()+" size is "+temp_set.size());
				if (temp_set.size() < size) {

					//System.out.println("Add?");
					//System.out.println("Rule"+rule.getNumber()+" size is "+temp_set.size());
					result_set.add(rule);
					result_set.addAll(deps_child_all.get(rule));
					list.remove(rule);
					list.removeAll(deps_child_all.get(rule));
					// list.remove(o)

				} else if (temp_set.size() == size) {
					result_set.add(rule);
					result_set.addAll(deps_child_all.get(rule));
					System.out.println("Remain list number is "+list.size());
					break while_loop;	
				} else {
					System.out.println("Remain list number is "+list.size());
					break while_loop;	
				}
			}





		}
	
		ArrayList<Rule> temp = new ArrayList<Rule> (result_set);
		for (Rule temp_rule : temp) {
			System.out.print(temp_rule.toString()+" ");
		}
		System.out.println();

	}


	private void cover_set_algo (int size, ArrayList<Rule> list) {

	}	
	
	private void LRU (int size) {
		
		
		
		LinkedList<Rule> cache = new LinkedList<Rule>(result_set);
<<<<<<< HEAD
		Collections.reverse(cache);
=======
		// sort the cache by weight
		Collections.sort(cache, new Comparator<Rule>() {

			@Override
			public int compare(Rule o1, Rule o2) {
				
				double o1_value;
				double o2_value;
				if (!o1.judge()) {
					o1_value = 0;
				} else {
					o1_value = (double) o1.getWeight()/(deps_child.get(o1).size()+1);
					// System.out.println(o1_value);
				}
				if (!o2.judge()) {
					o2_value = 0;
				} else {
					o2_value = (double) o2.getWeight()/(deps_child.get(o2).size()+1);
				}
				if (o2_value > o1_value) {						
					return 1;
				} else if (o2_value < o1_value) {						
					return -1;
				} 
				
				return 0;
			}
			
		});


		//Collections.reverse(cache);
>>>>>>> 515ec9d7ec7ee713b9dc5737c3dccb077ad5b7dc
		RuleQueue queue = new RuleQueue(cache, size);
		
		
		ArrayList<Rule> input = new ArrayList<Rule>(input_Rules);
		int hit_times = 0;
		
		System.out.println("First is "+queue.first().toString());
		System.out.println("Last is "+queue.last().toString());
		for (Rule print_rule : queue.getCache()) {
			System.out.print(print_rule.toString()+" ");
		}
		System.out.println();
		/**
		for (int i = 0; i<deps_child.get(input.get(3)).size(); i++) {
			System.out.println("Flag "+deps_child.get(input.get(3)).get(i).toString());
			// cover_number.add(deps_child.get(3).get(i).getNumber());
		}
		*/
		//boolean flag = true; 
		System.out.println(queue.getCount());
		for (Rule r: input )  {
			
			if (r.judge() && !queue.contain(r)) {
				boolean flag = true;
				while (flag) {
					// flag = true;
					
					int left = size - queue.getCache().size();
					
					ArrayList<Rule> check_cover_new = new ArrayList<Rule> (deps_child.get(r));
					ArrayList<Rule> cover_new_number = new ArrayList<Rule>(check_cover_new);
					ArrayList<Rule> added_set = new ArrayList<Rule>();
					added_set.add(r);
					
					/*
					for (Rule cover_new : check_cover_new) {
						
						cover_new_number.add(temp);
					} */
					
					int required_size = 1;
					
					if (deps_child_all.get(r).size() <= 1) {
						for (Rule rule_in_cache : cover_new_number) {
							
							Rule temp = new Rule("R"+rule_in_cache.getNumber()+"*", rule_in_cache.getPriority());
							
							if ((!queue.getCache().contains(rule_in_cache)) && (!queue.getCache().contains(temp))) {						
								added_set.add(rule_in_cache);
								required_size++;
							}
						}
					} else {
						/*
						for (Rule print_rule : queue.getCache()) {
							System.out.print(print_rule.toString()+" ");
						}
						System.out.println();
						*/
						for (Rule rule_in_cache : cover_new_number) {
							
							Rule temp = new Rule("R"+rule_in_cache.getNumber()+"*", rule_in_cache.getPriority());
							
							if ((!queue.getCache().contains(rule_in_cache)) && (!queue.getCache().contains(temp))) {	
								//System.out.println(temp.toString()+" Contain cover "+(queue.getCache().get(0).equals(temp)));
								//System.out.println(1);
								added_set.add(temp);
								required_size++;
							}
						}
					}
<<<<<<< HEAD
				}
				// the possible cover set of deleted rule
				System.out.println(r.toString());
				for (int i = 0; i<check_cover_last.size(); i++) {
					System.out.println(" cover "+check_cover_last.get(i).getNumber());
					cover_number.add(check_cover_last.get(i).getNumber());
				}
				
				for (Integer i: cover_number) {
					//System.out.println("Cover is Rule"+i+"*");
=======
>>>>>>> 515ec9d7ec7ee713b9dc5737c3dccb077ad5b7dc
					
					
					
					
					if (required_size <= left) {
						System.out.println("Could add rule");
						Collections.reverse(added_set);
						
						for (Rule added_rule : added_set) {
							// System.out.println("Size insertion is "+added_set.size());						
							queue.add_If_Miss(added_rule);
							
						}
						
						
						
						for (Rule print_rule : queue.getCache()) {
							System.out.print(print_rule.toString()+" ");
						}
						System.out.println();
						System.out.println("There is "+queue.getCount()+" times insertion");
						break;						
					}
					
					HashMap<Integer, Rule> map_cover = new HashMap<Integer, Rule>();
					cache = queue.getCache();
					
					Rule last = queue.last();
					
					ArrayList<Rule> check_cover_last = new ArrayList<Rule> (deps_child.get(last));
					ArrayList<Integer> cover_number = new ArrayList<Integer>();
					
					for (Rule cover_in_cache : cache) {
						// get all cover set
						if (!cover_in_cache.judge()) {
							 // System.out.println(" cover number is "+cover_in_cache.getNumber());
							map_cover.put(cover_in_cache.getNumber(), cover_in_cache);
						}
					}
					
					// the possible cover set of deleted rule
					// System.out.println("Added rule is "+r.toString());
					for (int i = 0; i<check_cover_last.size(); i++) {
						// System.out.println(" cover "+check_cover.get(i).getNumber());
						cover_number.add(check_cover_last.get(i).getNumber());
					}
					
					for (Integer i: cover_number) {
						//System.out.println("Cover is Rule"+i+"*");				
						// System.out.println(temp.toString());
						if (map_cover.containsKey(i)) {
							// System.out.println("Last Rule is "+last.toString()+" and the cover is "+map_cover.get(i).toString());
							queue.delete(map_cover.get(i));
						}
					}
					
					// Delete the last rule, and add its cover into the cache
					queue.delete(last);
					// Add all father rules.
					if (last.judge()) {
						queue.addFathers(deps_father_all.get(last).size());
						System.out.println("Rule is "+last.toString()+" Add fathers "+deps_father_all.get(last).size());
					}
					// 
					if (!deps_father.get(last).isEmpty() && !deps_child.get(last).isEmpty()){
						Rule cover_last = new Rule("R"+last.getNumber()+"*", last.getPriority());
						System.out.println("Add the deleted rule cover "+cover_last.toString());
						queue.add_If_Miss(cover_last);
					} 					
					
					
					
					
				}
				
			} else if (r.judge() && queue.contain(r)) {
				System.out.println("Added hitted rule");
				queue.add_If_Hitted(r);
				hit_times++;
			}
			
			
		}
		
		double hit_ratio = (double) hit_times/(input.size());
		System.out.println(input.size()+"The hit ratio is "+hit_ratio);
		
	}
	
	private void independent_set_algo (int size, ArrayList<Rule> list) {

		while_loop: while (true) {

			Collections.sort(list, new Comparator<Rule>() {

				public int compare(Rule o1, Rule o2) {

					ArrayList<Rule> duplicate_o2 = new ArrayList<Rule>();
					// ArrayList<Rule> current_result_set = new ArrayList<Rule>(result_set);
					duplicate_o2 = deps_child.get(o2);
					duplicate_o2.retainAll(result_set);

					ArrayList<Rule> duplicate_o1 = new ArrayList<Rule>();
					// ArrayList<Rule> current_result_set = new ArrayList<Rule>(result_set);
					duplicate_o1 = deps_child.get(o1);
					duplicate_o1.retainAll(result_set);

					double o2_value = ((double) o2.getWeight()) / ((double) 1+deps_child.get(o2).size()-duplicate_o2.size());
					double o1_value = ((double) o1.getWeight()) / ((double) 1+deps_child.get(o1).size()-duplicate_o1.size());

					if (o2_value > o1_value) {
						return 1;
					} else if (o2_value < o1_value) {
						return -1;
					}
					return 0;
				}

			});

			outer:

				for (int i = 0; i < list.size(); i++) {

					Rule rule = list.get(i);
					//System.out.println("Check Rule"+rule.getNumber());
					if (!result_set.contains(rule)){

						HashSet<Rule> temp_set = new HashSet<Rule>(result_set);

						temp_set.add(rule);
						temp_set.addAll(deps_child.get(rule));

						if (temp_set.size() < size) {

							//System.out.println("Add?");
							//System.out.println("Rule"+rule.getNumber()+" size is "+temp_set.size());
							result_set.add(rule);
							result_set.addAll(deps_child.get(rule));
							list.remove(rule);
							list.removeAll(deps_child.get(rule));
							// list.remove(o)
							break outer;
						} else if (temp_set.size() == size) {
							result_set.add(rule);
							result_set.addAll(deps_child.get(rule));
							System.out.println("Remain list number is "+list.size());
							break while_loop;	
						} else {
							System.out.println("Remain list number is "+list.size());
							break while_loop;	
						}
						// 此处可能有bug

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

				if (result_set.containsAll(deps_child.get(list.get(i))) && size > 0) {
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
						size = size - (deps_child.get(list.get(i))).size()-1;
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

		list = (ArrayList<Rule>) deps_child.get(rule).clone();
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
				parents = (ArrayList<Rule>) deps_child.get(temp).clone();

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
				combine.addAll(deps_child.get(ruleSet.get(i)));
				weight = ruleSet.get(i).getWeight()+weight;
			}
			Set<Rule> set  = new HashSet<Rule>(combine);
			ArrayList<Rule> all = new ArrayList<Rule>();
			all.addAll(set);

			ArrayList<Rule> next = new ArrayList<Rule>();
			next = (ArrayList<Rule>) deps_child.get(max_rule).clone();

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

				list.removeAll(deps_child.get(max_rule));

				list.addAll(deps_child.get(max_rule));


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
	/**
	 * Implement three Map
	 * 	1. Rule map to corresponding direct dependency children rules
	 * 	2. Rule map to corresponding all dependent rules
	 * 	2. Rule map to corresponding direct father rules
	 * @param rule
	 * @param parent
	 * @return
	 */
	public Map<Rule, ArrayList<Rule>> addParents(Rule rule, ArrayList<Rule> parent) {

		Collections.sort(parent, new Comparator<Rule>() {

			public int compare(Rule o1, Rule o2) {

				return o2.getPriority()-o1.getPriority();
			}

		});

		ArrayList<Pair> source_range = new ArrayList<Pair>();
		source_range.add(rule.getSourceRange());

		ArrayList<Pair> des_range = new ArrayList<Pair>();
		des_range.add(rule.getDesRange());
		//System.out.println("Test Rule"+rule.getNumber());
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


			// System.out.println("all child Test Rule"+rule.getNumber());

			start:
				if ( match (source_ip_r1, source_ip_r2, source_mask_r1, source_mask_r2) && 
						match (des_ip_r1, des_ip_r2, des_mask_r1, des_mask_r2) &&
						!deps_child.get(rule).contains(rj)) {

					/*
					 * Get all dependent children rules
					 */
					//System.out.println("True for Rule"+rj.getNumber());
					HashSet<Rule> deps_temp = new HashSet<Rule>();
					if (deps_child_all.containsKey(rule)) {
						deps_temp = new HashSet<Rule> (deps_child_all.get(rule));
					}
					HashSet<Rule> rj_children = new HashSet<Rule>  (deps_child_all.get(rj));
					deps_temp.add(rj);
					deps_temp.addAll(rj_children);
					ArrayList<Rule> rule_children_temp = new ArrayList<Rule> (deps_temp);
					deps_child_all.put(rule, rule_children_temp);
					
					HashSet<Rule> deps_temp_father = new HashSet<Rule>();
					if (deps_father_all.containsKey(rj)) {
						deps_temp_father = new HashSet<Rule> (deps_father_all.get(rj));
					}
					HashSet<Rule> rule_father = new HashSet<Rule>  (deps_father_all.get(rule));
					deps_temp_father.add(rule);
					deps_temp_father.addAll(rule_father);
					ArrayList<Rule> rule_father_temp = new ArrayList<Rule> (deps_temp_father);
					deps_father_all.put(rj, rule_father_temp);
					
					
					
					/*
					 * Get all direct dependent children rules and direct dependent father rules
					 */
					Long rj_source_min = rj.getSourceRange().getMin();
					Long rj_source_max = rj.getSourceRange().getMax();
					Long rj_des_min = rj.getDesRange().getMin();
					Long rj_des_max = rj.getDesRange().getMax();

					boolean flag_1 = false;
					boolean flag_2 = false;
					//System.out.println("Rule"+rule.getNumber()+" is under test and the size is "+source_range.size());
					//System.out.println("Rule"+rj.getNumber()+" is in");

					outer_source:
						for (int x = 0; x < source_range.size(); x++) {
							Long rule_source_min = source_range.get(x).getMin();
							Long rule_source_max = source_range.get(x).getMax();
							//System.out.println(rj_source_min+" "+rule_source_min+" "+rj_source_max+" "+rule_source_max);
							if (rj_source_min.equals(0L) && rj_source_max.equals(4294967295L) && source_range.size()>0) {
								flag_1 = true;
								source_range.removeAll(source_range);
								break outer_source;

							}
							if (rj_source_min > rule_source_min && rj_source_max < rule_source_max) {

								flag_1 = true;
								source_range.remove(x);
								Pair added_1 = new Pair(rule_source_min, rj_source_min-1);
								Pair added_2 = new Pair(rj_source_max+1, rule_source_max);
								source_range.add(added_1);
								source_range.add(added_2);
								break outer_source;
							} else if (rj_source_min.equals(rule_source_min) && rj_source_max < rule_source_max) {

								flag_1 = true;
								source_range.remove(x);
								Pair added_1 = new Pair(rj_source_max+1, rule_source_max);

								source_range.add(added_1);

								break outer_source;
							} else if (rj_source_min > rule_source_min && rj_source_max.equals(rule_source_max) ) {

								flag_1 = true;
								source_range.remove(x);
								Pair added_1 = new Pair(rule_source_min, rj_source_min-1);

								des_range.add(added_1);
								break outer_source;
							}else if ( (rj_source_min.equals(rule_source_min) && rj_source_max.equals(rule_source_max)) ) {
								flag_1 = true;
								source_range.remove(x);
								break outer_source;
							} 
							flag_1 = false;

						}

					//System.out.println("Rule"+rj.getNumber()+" go out 1");
					outer_des:
						for (int x = 0; x < des_range.size(); x++) {
							Long rule_des_min = des_range.get(x).getMin();
							Long rule_des_max = des_range.get(x).getMax();
							//System.out.println(""+rj_des_min+" and "+rj_des_max);

							if (rj_des_min.equals(0L) && rj_des_max.equals(4294967295L)&des_range.size()>0) {
								flag_2 = true;
								des_range.removeAll(des_range);
								break outer_des;
							}
							if (rj_des_min > rule_des_min && rj_des_max < rule_des_max) {

								flag_2 = true;
								des_range.remove(x);
								Pair added_1 = new Pair(rule_des_min, rj_des_min-1);
								Pair added_2 = new Pair(rj_des_max+1, rule_des_max);
								des_range.add(added_1);
								des_range.add(added_2);
								break outer_des;
							} else if (rj_des_min.equals(rule_des_min) && rj_des_max < rule_des_max) {

								flag_2 = true;
								des_range.remove(x);
								Pair added_1 = new Pair(rj_des_max+1, rule_des_max);

								des_range.add(added_1);

								break outer_des;
							} else if (rj_des_min > rule_des_min && rj_des_max.equals(rule_des_max)) {

								flag_2 = true;
								des_range.remove(x);
								Pair added_1 = new Pair(rule_des_min, rj_des_min-1);

								des_range.add(added_1);
								break outer_des;
							}else if ((rj_des_min.equals(rule_des_min) && rj_des_max.equals(rule_des_max))) {
								flag_2 = true;
								des_range.remove(x);
								break outer_des;
							} 
							flag_2 = false;
							//break start;
						}
						boolean flag = false;
						if (!flag_1 && flag_2) {
							if (rj_source_min >= rule.getSourceRange().getMin() && rj_source_max <= rule.getSourceRange().getMax() ) {
								flag = true;
							}
						} else if (flag_1 && !flag_2) {
							if (rj_des_min >= rule.getDesRange().getMin() && rj_des_max <= rule.getDesRange().getMax() ) {
								flag = true;
							}
						} else if (flag_1 && flag_2) {
							flag = true;
						} else {
							flag = false;
						}


						if (!flag) {
							break start;
						}
						//System.out.println("Rule"+rj.getNumber()+" go out 2");
						if (flag) {

							HashSet<Rule> dep_temp = new HashSet<Rule> (deps_child.get(rule));
							dep_temp.add(rj);
							ArrayList<Rule> rule_children = new ArrayList<Rule> (dep_temp);
							deps_child.put(rule, rule_children);

							dep_temp = new HashSet<Rule> (deps_father.get(rj));
							dep_temp.add(rule);
							ArrayList<Rule> rj_father = new ArrayList<Rule> (dep_temp);
							deps_father.put(rj, rj_father);

						}

						/*
				HashSet<Rule> dep_temp = new HashSet<Rule>();
				if (deps_child.containsKey(rule)) {

					dep_temp = new HashSet<Rule>(deps_child.get(rule));

				} 	
				HashSet<Rule> rj_children = new HashSet<Rule>(deps_child.get(rj));
				dep_temp.add(rj);
				dep_temp.addAll(rj_children);
				ArrayList<Rule> rule_children = new ArrayList<Rule> (dep_temp);
				deps_child.put(rule, rule_children);
						 */


				} 


		} 
		Collections.sort(deps_child.get(rule));
		/*
		for (int i = 0; i < parent.size(); i++) {
			if (parent.get(i).getLevel()+1 == rule.getLevel() && !deps.get(rule).contains(parent.get(i))) {
				parent.get(i).increaseLevel();
			}
		}
		 */
		return deps_child;

	}

	/**
	 * Function to test dependency between two rules
	 * @param ip1
	 * @param ip2
	 * @param mask1
	 * @param mask2
	 * @return
	 */
	public boolean match (Long ip1, Long ip2, int mask1, int mask2) {

		int mask_short;
		//int ip1_int = ip1;

		if (mask1 > mask2) {
			mask_short = mask2;
		} else {
			mask_short = mask1;
		}

		//System.out.println(ip1+"  "+ip2);
		//System.out.println(mask_short);
		//System.out.println((ip1 >> (32-mask_short)) +"    "+(ip2 >> (32-mask_short)));
		//System.out.println((ip1 >> (32-mask_short)) == (ip2 >> (32-mask_short)));
		return ((ip1 >> (32-mask_short)) == ((ip2>> (32-mask_short))));
	}

	/**
	 * Function: Read content from .txt file and get the input rules
	 * 
	 * Step:
	 * 	1. Grasp each line of the file. 
	 * 	2. Get the source and destination IP address and transfer them into decimal form
	 * 	3. Create a new RULE and add it into list
	 * 
	 * Note: Consider the situation where no such file, reading error.
	 * @param filePath
	 * @param trace TODO

	 */

	public static void readTxtFile(String filePath, ArrayList<Rule> list, Map<ArrayList<String>, Integer> trace){

		try {

			String encoding="GBK";

			File file=new File(filePath);

			if(file.isFile() && file.exists()){ 

				InputStreamReader read = new InputStreamReader(

						new FileInputStream(file),encoding);

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

					Pair source_range = ip2range (source[0], source_mask);
					Pair des_range = ip2range (target[0], des_mask);

					//System.out.println(source_ip+"\t"+des_ip);
					// System.out.println("des ip is "+des_ip);
					String combine = source_ip+des_ip;
					
					// Remove duplicate rules
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


					System.out.println("Rule"+(i+1)+" Weight is "+weight);
					Rule r = new Rule(source_ip, des_ip, source_range, des_range, i, weight, source_mask, des_mask);
					relation_Rule.put(combine, r);
					//System.out.println("Rule"+(i+1)+" range is "+source_range.getMin().toString()+" to "+source_range.getMax().toString());
					list.add(r);
					i = i+ 1;
				}

				read.close();

			}else{
				System.out.println("No such file");
			}
		} catch (Exception e) {
			System.out.println("Reading error");
			e.printStackTrace();
		}
	}

	/**
	 * The function aims to generate the weight of each rule in the trace file
	 * @param filePath
	 * @param trace
	 */
	public static void readTraceFile(String filePath, Map<ArrayList<String>, Integer> trace){

		try {

			String encoding="GBK";

			File file=new File(filePath);

			if(file.isFile() && file.exists()){ 

				InputStreamReader read = new InputStreamReader(

						new FileInputStream(file),encoding);

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
				System.out.println("No such file");
			}
		} catch (Exception e) {
			System.out.println("Reading error");
			e.printStackTrace();
		}
	}
	
	public static void readInputTrace(String filePath){

		try {

			String encoding="GBK";

			File file=new File(filePath);

			if(file.isFile() && file.exists()){ 

				InputStreamReader read = new InputStreamReader(

						new FileInputStream(file),encoding);

				BufferedReader bufferedReader = new BufferedReader(read);

				String lineTxt = null;
				int i = 0;
				while((lineTxt = bufferedReader.readLine()) != null){

					

					String[] temp = lineTxt.split("\n");
					// Each component
					String[] temp_for = temp[0].split("\t");

					String source = temp_for[0];
					String target = temp_for[1];
					String combine = source+target;
					System.out.println(i);
					i++;
					
					if (relation_Rule.containsKey(combine)) {
						input_Rules.add(relation_Rule.get(combine));
					}

					// System.out.println("source ip is "+source+" and des ip is "+target);

					// total_trace++;
				}



				read.close();

			}else{
				System.out.println("No such file");
			}
		} catch (Exception e) {
			System.out.println("Reading error");
			e.printStackTrace();
		}
	}

	/**
	 * Transfer the IP address into binary form
	 * @param ip
	 * @return
	 */
	public static String ip2int(String ip){ 
		String[] items = ip.split("\\.");

		Long num = Long.valueOf(items[0])<<24 
				|Long.valueOf(items[1])<<16 
				|Long.valueOf(items[2])<<8 
				|Long.valueOf(items[3]); 
		String binary = Long.toString(num);

		return binary; 
	} 

	/**
	 * The function aims to get the cover range of one IP address with wildcard charter. 
	 * @param ip
	 * @param mask
	 * @return
	 */
	public static Pair ip2range(String ip, int mask){ 
		String[] items = ip.split("\\.");

		Long num = Long.valueOf(items[0])<<24 
				|Long.valueOf(items[1])<<16 
				|Long.valueOf(items[2])<<8 
				|Long.valueOf(items[3]); 
		String binary = Long.toBinaryString(num);

		//System.out.println("binary string is "+binary);
		if (binary.equals("0")) {
			//System.out.println("flag");
			char[] lower = {'1', '1', '1', '1','1', '1', '1', '1','1', '1', '1', '1','1', '1', '1', '1','1', '1', '1', '1','1', '1', '1', '1','1', '1', '1', '1','1', '1', '1', '1'};
			binary = String.valueOf(lower);

		}

		char[] char_list_lower= binary.toCharArray();
		char[] char_list_uppper= binary.toCharArray();

		for (int i = mask; i < binary.length(); i++) {
			char_list_lower[i]  = '0';
			char_list_uppper[i] = '1';
		} 

		String lower = new String(char_list_lower);
		String upper = new String (char_list_uppper);
		//System.out.println(upper);
		Long lower_bound = Long.valueOf(lower, 2);
		Long upper_bound = Long.valueOf(upper, 2);

		Pair range = new Pair(lower_bound, upper_bound);
		return range; 
	} 



	public static void main(String[] args) {
		new Construct();

		System.out.println("The end"); 

	}

}
