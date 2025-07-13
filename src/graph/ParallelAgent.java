package graph;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ParallelAgent implements Agent{
	
	public final Agent agent; //wrapped agent
	public final BlockingQueue<MessageWrapper> messages; //queue of message wrappers allows use of poison pills for graceful exiting
	public final Thread activeThread; //active object runs thread for dequeuing BlockingQueue
	private volatile boolean running; //for brute force closing in case of exception
	
	
	//constructor
	public ParallelAgent(Agent agent, int capacity) {
		this.agent = agent;
		messages = new ArrayBlockingQueue<MessageWrapper>(capacity);
		running = true;
		activeThread = new Thread (() -> {
			MessageWrapper messageWrapper; //holder for dequeued item 
			do {
				try {
					messageWrapper = messages.take();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					messageWrapper = MessageWrapper.POISON_PILL; //in case of exception - allow graceful closure
				} 
				if(!messageWrapper.IsPoisonPill) {
					agent.callback(messageWrapper.topic, messageWrapper.message);
				}
			}while(!messageWrapper.IsPoisonPill && running);
		});
		activeThread.start();
	}

	@Override
	//use wrapped agent's getName()
	public String getName() {
		// TODO Auto-generated method stub
		return agent.getName();
	}

	@Override
	//use wrapped agent's reset()
	public void reset() {
		// TODO Auto-generated method stub
		agent.reset();
	}

	@Override
	//override agent's callback() to allow queuing items without waiting
	public void callback(String topic, Message message) {
		// TODO Auto-generated method stub
		try {
			messages.put(new MessageWrapper(topic, message));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	//override agent's close() in order to properly close the active object
	public void close() {
		// TODO Auto-generated method stub
		running = false; //for brute force closure in case of exception
		try {
			messages.put(MessageWrapper.POISON_PILL); //attempt graceful closure
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			activeThread.interrupt(); //brute force closure where necessary
		}
		try {
			activeThread.join(); //close active thread
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		agent.close(); //close wrapped agent
		
	}
    
	//private class to allow graceful closure of active thread using poison pill
	private static class MessageWrapper{
		Message message;
		String topic;
		boolean IsPoisonPill;
		
		public MessageWrapper(String topic, Message message) {
			this.topic = topic;
			this.message = message;
			this.IsPoisonPill = false; //default for non-poison pill
		}
		
		private MessageWrapper() {
			this.topic = null;
			this.message = null;
			IsPoisonPill = true;
		}
		
		//poison pill (unique)
		public static MessageWrapper POISON_PILL = new MessageWrapper();
	}
}
