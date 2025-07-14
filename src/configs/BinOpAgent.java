package configs;

import graph.*;
import java.util.function.BinaryOperator;

/**
 * an agent which runs an arithmetic operation on two operators 
 */
public class BinOpAgent implements Agent{
    private final String name;
    private final String in1;
    private final String in2;
    private final String out;
    private final BinaryOperator<Double> op;
    
    //inputs
    private Double val1;
    private Double val2;
    private Double res;
    
    //constructor
	public BinOpAgent(String name, String in1, String in2, String out, BinaryOperator<Double> op) {
        this.name = name;
        this.in1 = in1;
        this.in2 = in2;
        this.out = out;
        this.op = op;
		val1 = 0.0;
		val2 = 0.0;
		res = op.apply(val1, val2);
        
        TopicManagerSingleton.get().getTopic(in1).subscribe(this);
        TopicManagerSingleton.get().getTopic(in2).subscribe(this);
        TopicManagerSingleton.get().getTopic(out).addPublisher(this);
    }
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void reset() {
		val1 = 0.0;
		val2 = 0.0;
		calcAndPublish();
	}

	@Override
	public void callback(String topic, Message msg) {
		if (topic.equals(in1)) {
            val1 = msg.asDouble;
        } else if (topic.equals(in2)) {
            val2 = msg.asDouble;
        }
		calcAndPublish();
	}

	@Override
	public void close() {
		TopicManagerSingleton.get().getTopic(in1).unsubscribe(this);
        TopicManagerSingleton.get().getTopic(in2).unsubscribe(this);
        TopicManagerSingleton.get().getTopic(out).removePublisher(this);
	}
	
	private void calcAndPublish() {
		res = op.apply(val1, val2);
		TopicManagerSingleton.get().getTopic(out).publish(new Message(res));
	}
}
