package com.triona.solution;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.triona.company.Worker;



/**
 * 
 * Feel free to use java 7 or java 8 idioms
 *
 */
public class Solutions {

    private List<Worker> workers;

    public Solutions() {

	workers = Arrays.asList(
		new Worker("anne", 20, true, false, ""), 
		new Worker("barry", 27, false, true, "anne"), 
		new Worker("catherine", 27, false, true, "anne"), 
		new Worker("david", 28, true,false, ""), 
		new Worker("eric", 30, false, true, "david"), 
		new Worker("fiona", 27, false, true, "david"), 
		new Worker("gary", 21, true, false, ""), 
		new Worker("hannah", 19, false, true,"gary"), 
		new Worker("ian", 22, true, false, ""), 
		new Worker("john", 44, false, true, "ian"), 
		new Worker("kevin", 31, false, true, "ian"), 
		new Worker("leanne", 30, false, true, "ian"),
		new Worker("michelle", 28, false, true, "ian"));

	System.out.println("************************************");
	System.out.println("All workers");
	printAllWorkers();
	System.out.println("************************************");
	System.out.println("All leaders");
	printTeamLeaders();
	System.out.println("************************************");
	System.out.println("Members younger than 30");
	printTeamMembersYoungerThan(30);
	System.out.println("************************************");
	System.out.println("Members younger than 20");
	printTeamMembersYoungerThan(20);
	System.out.println("************************************");
	System.out.println("Oldest worker");
	printOldestWorker();
	System.out.println("************************************");
	System.out.println("3 youngest workers");
	printTheThreeYoungestWorkers();
	System.out.println("************************************");
	System.out.println("Annes team");
	printMembersForLeader(getWorkerByName("anne"));
	System.out.println("************************************");
	System.out.println("Ians team");
	printMembersForLeader(getWorkerByName("ian"));
	System.out.println("************************************");
	System.out.println("Average age");
	printAverageAge();
	System.out.println("************************************");
	System.out.println("Leader with most members");
	printLeaderWithMostMembers();
	System.out.println("************************************");
	System.out.println("Leader with youngest team");
	printLeaderWithYoungestTeam();

    }

    public static void main(String[] args) {
	new Main();
    }

    public void printAllWorkers() {
	for (Worker worker : getWorkers()) {
	    System.out.println(worker);
	}
    }

    /**
     * Should print out team leaders only
     */
    public void printTeamLeaders() {
	workers.stream().filter(w -> w.isTeamLeader()).forEach(w -> System.out.println(w.toString()));
    }

    /**
     * Prints out all team members (not managers) younger than a specified age
     * 
     * @param age
     *            - age in years
     */
 public void printTeamMembersYoungerThan(int age) {
	
    	Predicate<Worker> isTeamMember = w -> w.isTeamMember(); //filter1 - team member (not manager)
    	Predicate<Worker> isYoungerThanSpecifiedAge = e -> e.getAge() < age; //filter2 - younger than a specified age
    	getWorkers()
    		.stream()
    		.filter(isTeamMember.and(isYoungerThanSpecifiedAge))
    		.map(w -> w.getName())
    		.forEach(System.out::println);
    }

    /**
     * Prints out the oldest worker (includes members and leaders)
     */
    public void printOldestWorker() {
	Worker oldest = workers.stream().sorted(Comparator.comparing(w -> w.getAge())).collect(Collectors.toList()).get(workers.size()-1);
	System.out.println(oldest.toString());
    }

    /**
     * Prints out the three youngest workers (includes members and leaders)
     */
    public void printTheThreeYoungestWorkers() {
	List<Worker> youngestWorkers = workers.stream().sorted(Comparator.comparing(w -> w.getAge())).collect(Collectors.toList()).subList(0, 3);
	youngestWorkers.stream().forEach(w -> System.out.println(w.toString()));

    }

    /**
     * Prints out the members who report to the given worker
     * 
     * @param manager
     *            - a worker (may be a member or a leader)
     */
    public void printMembersForLeader(Worker worker) {
	// if this worker is not a manager - print an error
	Predicate<Worker> teamMembers = w -> w.getManagerName().equalsIgnoreCase(worker.getName());
	if(worker.isTeamLeader()){
	    workers.stream().filter(teamMembers).forEach(tm -> System.out.println(tm.toString()));
	}else{
	    System.err.println("ERROR: "+worker.getName()+" is not a team leader");
	}
    }

    /**
     * Get the average of all worker ages
     */
    public void printAverageAge() {
	
	double averageAge = workers.stream()
		.mapToInt((w) -> w.getAge())
		.average()
		.getAsDouble();
	System.out.println(Math.round(averageAge));
    }

    /**
     * Print the leader with the most members
     */
    public void printLeaderWithMostMembers() {
	//get all managers (names)
	List<String> allManagers = workers.stream()
		.map(w -> w.getManagerName())
		.filter(value -> !value.isEmpty())
		.distinct()
		.collect(Collectors.toList()); 
	
	//count no of workers each manager has
	String largestTeam_leader;
	Map<String,Long> teamSize = new HashMap<String,Long>();
		
	for(String manager: allManagers){
	    teamSize.put(manager, workers.stream().filter(w -> w.getManagerName().equalsIgnoreCase(manager)).count());
	}
	
	 largestTeam_leader = teamSize.entrySet().stream().max((mangA, mangB) -> mangA.getValue() > mangB.getValue() ? 1 : -1).get().getKey();
	 System.out.println(largestTeam_leader);
    }

    /**
     * Print the leader with the youngest team - use the mean age of the
     * MEMBERS to determine team age
     */
    public void printLeaderWithYoungestTeam() {
    	String teamLeader_with_youngestTeam = getWorkers().stream() 
			.filter(w -> w.isTeamMember()) // filter out team members
			.collect(Collectors.groupingBy(Worker::getManagerName )) //group by manager name (this returns a map of manager names:list of workers)
			.entrySet() //get the entrySet to carry out more stream logic
			.stream() //convert entry set to stream
			.min((t1,t2) -> Double.compare( //find the min value using a comparator
					t1.getValue().stream().mapToInt(m -> m.getAge()).average().getAsDouble(),  //get the average age of team1
					t2.getValue().stream().mapToInt(m -> m.getAge()).average().getAsDouble()   //get the average age of team2
					)
			)
			.get() //get 1st entry
			.getKey(); // get the key of the first entry which will be the manager name
	System.out.println(teamLeader_with_youngestTeam);
    }

    public List<Worker> getWorkers() {
	return workers;
    }

    public void setWorkers(List<Worker> workers) {
	this.workers = workers;
    }

    /**
     * Returns a worker with a given name
     * 
     * @param name
     *            - name of the worker you wish to find
     * @return
     */
    public Worker getWorkerByName(String name) {
	Optional<Worker> opt = getWorkers().stream().filter(w -> w.getName().equalsIgnoreCase(name)).findFirst();
	return opt.isPresent() ? opt.get() : null; // if the filter and find operations fail, return null
    }

}
