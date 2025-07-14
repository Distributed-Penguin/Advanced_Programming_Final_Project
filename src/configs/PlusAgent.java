package configs;

import graph.*;

/**
 * PlusAgent is essentially a BinOpAgent with internal variables called x and y
 */
public class PlusAgent implements Agent  {
	
	private final String name;
    private final String in1;
    private final String in2;
    private final String out;
    private static int uid = 0; //uid to differentiate PlusAgents
    
	private double x;
	private double y;
    private Double res;
	
    //constructor
    public PlusAgent(String[] subs, String[] pubs) {
    	this.name = "PlusAgent_" + uid++;
    	this.x = 0.0;
		this.y = 0.0;
		this.res = 0.0;
        
		in1 = subs[0];
        in2 = subs[1];
        out = pubs[0];
        
		TopicManagerSingleton.get().getTopic(in1).subscribe(this);
        TopicManagerSingleton.get().getTopic(in2).subscribe(this);
        TopicManagerSingleton.get().getTopic(out).addPublisher(this);
    }
    
    //returns agent name
    @Override
    public String getName() {
        return name;
    }

    //reset inputs
    @Override
    public void reset() {
        x = 0.0;
        y = 0.0;
        addAndPublish();
    }

    //observer callback
    @Override
	public void callback(String topic, Message msg) {
		if (topic.equals(in1)) {
            x = msg.asDouble;
        } else if (topic.equals(in2)) {
            y = msg.asDouble;
        }
		addAndPublish();
	}
    
    //close topics
    @Override
	public void close() {
		TopicManagerSingleton.get().getTopic(in1).unsubscribe(this);
        TopicManagerSingleton.get().getTopic(in2).unsubscribe(this);
        TopicManagerSingleton.get().getTopic(out).removePublisher(this);
	}
    
    //helper calc and publish function
    private void addAndPublish() {
		res = x + y;
		TopicManagerSingleton.get().getTopic(out).publish(new Message(res));
	}
    
    //getters
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
}
