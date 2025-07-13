package configs;

import graph.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Graph extends ArrayList<Node>{
    
	private static final long serialVersionUID = 1L;
	public Graph() {
        super();
    }
	
    public boolean hasCycles() {
    	for (Node node : this) {
            if (node.hasCycles()) {
                return true;
            }
        }
        return false;
    }
    public void createFromTopics(){
    	HashMap<String, Node> nodeMap = new HashMap<>();
        for (Topic topic : TopicManagerSingleton.get().getTopics()) {
            // Create node for the topic
            String topicNodeName = "T" + topic.name;
            Node topicNode = nodeMap.computeIfAbsent(topicNodeName, Node::new);

            // Topic - Agent (subscriber)
            for (Agent agent : topic.getSubs()) {
                String agentNodeName = "A" + agent.getName();
                Node agentNode = nodeMap.computeIfAbsent(agentNodeName, Node::new);
                topicNode.addEdge(agentNode);
            }

            // Agent - Topic (publisher)
            for (Agent agent : topic.getPubs()) {
                String agentNodeName = "A" + agent.getName();
                Node agentNode = nodeMap.computeIfAbsent(agentNodeName, Node::new);
                agentNode.addEdge(topicNode);
            }
        }

        this.clear(); // in case we're reusing the Graph object
        this.addAll(nodeMap.values());
    }    

    
}
