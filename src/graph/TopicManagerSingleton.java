package graph;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

//maintain exclusive TopicManager as singleton
public class TopicManagerSingleton {

	//static class within class to utilize concurrent lazy programming of singleton
	public static class TopicManager{
    	
		private static final TopicManager instance = new TopicManager();//exclusive TopicManager (no one else can access constructor)
    	private final ConcurrentHashMap<String, Topic> topics;//ConcurrentHashMap implicitly guarantees thread-safety
    	
    	private TopicManager() {
    		topics = new ConcurrentHashMap<>();
    	}
    	
    	//return some topic from map or create new topic if not in topics map and then return it
    	public Topic getTopic(String name) {
            return topics.computeIfAbsent(name, Topic::new);
        }
        
    	//return collection of all topics
        public Collection<Topic> getTopics() {
            return topics.values();
        }
        
        //clear topics' map
        public void clear() {
            topics.clear();
        }
        
    }
	
	//TopicManager getter instantiates the instance variable only when called for the first time
	public static TopicManager get(){
		return TopicManager.instance;
	}
    
}
