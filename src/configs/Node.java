package configs;

import graph.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * this class realizes graph nodes which are characterized by a name, message and list of neighbors
 */
public class Node {
    private String name;
	private List<Node> edges; //neighbors list
    private Message msg;
    
    public Node(String name) {
		super();
		this.name = name;
		this.edges = new ArrayList<Node>();
		this.msg = null;
	}
    
    //adds a  node to 'edges' list
    public void addEdge(Node node) {
    	edges.add(node);
    }
    
    // Detect cycles using DFS
    public boolean hasCycles() {
        Set<Node> visited = new HashSet<>();
        Set<Node> recursionStack = new HashSet<>();
        return this.hasCyclesDFS(visited, recursionStack);
    }
    
    //hasCycles helper
    boolean hasCyclesDFS(Set<Node> visited, Set<Node> stack) {
        //if completes a cycle
    	if (stack.contains(this)) {
            return true;
        }
    	//if previously visited and didn't complete a cycle
        if (visited.contains(this)) {
            return false;
        }
        //if not previously visited and doesn't complete a cycle, run DFS:
        stack.add(this);
        //run DFS for each neighbor
        for (Node neighbor : this.getEdges()) {
            if (neighbor.hasCyclesDFS(visited, stack)) {
                return true;
            }
        }
        stack.remove(this);
        visited.add(this);
        return false;
    }
    
    //getters
	public String getName() {
		return name;
	}
	public List<Node> getEdges() {
		return edges;
	}
	public Message getMsg() {
		return msg;
	}
	
	//setters
	public void setName(String name) {
		this.name = name;
	}
	public void setEdges(List<Node> edges) {
		this.edges = edges;
	}
	public void setMsg(Message msg) {
		this.msg = msg;
	}
    
}