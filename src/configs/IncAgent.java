package configs;

import graph.*;

/**
 * For this class I wrap a PlusAgent and set one of its inputs to 1
 */
public class IncAgent implements Agent {
	
	private final String name;
    private final String input;
    private final String output;
    private static int uid = 0; //uid to differentiate IncAgents
    
    private double x;

    public IncAgent(String[] subs, String[] pubs) {
    	this.name = "IncAgent_" + uid++;
        this.input = subs[0];
        this.output = pubs[0];
        
        this.x = 0.0;
        
        TopicManagerSingleton.get().getTopic(input).subscribe(this);
        TopicManagerSingleton.get().getTopic(output).addPublisher(this);
    }
	
    //name getter
	@Override
	public String getName() {
        return name;
    }
	
	//reset input
    @Override
    public void reset() {
        x = 0.0;
        publishIncrement();
    }
    
    //observer read input
    @Override
    public void callback(String topic, Message msg) {
        if (topic.equals(input)) {
            x = msg.asDouble;
            publishIncrement();
        }
    }
    
    //standard close
    @Override
    public void close() {
        TopicManagerSingleton.get().getTopic(input).unsubscribe(this);
        TopicManagerSingleton.get().getTopic(output).removePublisher(this);
    }
    
    //helper increment and publish input
    private void publishIncrement() {
    	double res = x + 1;
        TopicManagerSingleton.get().getTopic(output).publish(new Message(res));
    }    
}
