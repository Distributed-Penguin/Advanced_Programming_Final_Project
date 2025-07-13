package graph;

import java.util.ArrayList;
import java.util.List;

//notify list of observers with messages from a list of publishers
public class Topic {
	
    public final String name;
    private final List<Agent> subs;
    private final List<Agent> pubs;
    private Message LastMessage;
    
    Topic(String name){
        this.name=name;
        subs = new ArrayList<>();
        pubs = new ArrayList<>();
        LastMessage = new Message("");
    }
    
    public Message getLastMessage() {
    	return LastMessage;
	}

	//add observer
    public void subscribe(Agent a){
    	subs.add(a);
    }
    
    //remove observer
    public void unsubscribe(Agent a){
    	subs.remove(a);
    }
    
    //notify all observers of new message from some publisher
    public void publish(Message m){
    	LastMessage = m;
    	for(Agent a : subs) {
    		a.callback(name,  m);
    	}
    }
    
    public String getName() {
		return name;
	}

	public List<Agent> getSubs() {
		return subs;
	}

	public List<Agent> getPubs() {
		return pubs;
	}

	//add publisher
    public void addPublisher(Agent a){
    	pubs.add(a);
    }
    
    //remove publisher
    public void removePublisher(Agent a){
    	pubs.remove(a);
    }


}
