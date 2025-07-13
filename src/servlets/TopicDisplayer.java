package servlets;

import graph.*;
import server.RequestParser.RequestInfo;

import java.io.*;
import java.util.Collection;

public class TopicDisplayer implements Servlet {
	
	//handle HTTP GET "/publish" request - update and display topics
    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
        PrintWriter out = new PrintWriter(toClient);
        try {
            String topicName = ri.getParameters().get("topic");
            String value = ri.getParameters().get("value");

            if (topicName != null && value != null) {
            	Collection<Topic> allTopics = TopicManagerSingleton.get().getTopics();
            	Boolean topicExists = false;
            	//The following loop checks if the topic exists. Checking this condition costs O(n) because all names are encapsulated in
            	//their respective topics. We could skip this check if the problem is large because the worst case is an unrelated topic will
            	//be created and appear in the side-table, but not effect the functionality of the graph
            	for (Topic topic : allTopics) {
            		if (topic.getName().equals(topicName)) {
                        topicExists = true;
                        break;
                    }
            	}
            	if (topicExists) {
                	Topic topic = TopicManagerSingleton.get().getTopic(topicName);
                    topic.publish(new Message(Double.parseDouble(value))); //I parse as double, because agents output double, so this maintains view uniformity
            	}
            }
            

            // Display all topic values as HTML table
            out.println("HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n");
            out.println("<html><head><title>Topic Values</title>");
            out.println("<style>");
            out.println(".section-title { font-size: 20px; font-weight: 600; margin: 0; padding: 12px 0 8px 10px; text-align: left; border-bottom: 1px solid #ccc; }");
            out.println("body { font-family: Arial, sans-serif; margin: 0; padding: 10px; text-align: left; }");
            out.println("table { border-collapse: collapse; margin-left: 10px; }");
            out.println("th, td { border: 1px solid black; padding: 4px 8px; }");
            out.println("</style></head><body>");

            out.println("<h2 class='section-title'>Topic Values</h2>");
            out.println("<table><tr><th>Topic</th><th>Value</th></tr>");

            for (Topic t : TopicManagerSingleton.get().getTopics()) {
                String name = t.getName();
                Message m = t.getLastMessage();
                String val = (m != null) ? m.asText : "(no value)";
                out.println("<tr><td>" + name + "</td><td>" + val + "</td></tr>");
            }

            out.println("</table></body></html>");
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            out.println("HTTP/1.1 500 Internal Server Error\r\n\r\n");
            out.flush();
        }
    }

    @Override
    public void close() throws IOException {}
}
/*
// Publish to the topic - this hypothetically allows the user to add new and unrelated existing topics, 
//but  avoiding this costs O(n) for each update, because we would need to check each 
//topic's name. This is not worth the tradeoff in my opinion (no harm done by adding dummy topics)
if (topicName == null || value == null) {
                out.println("HTTP/1.1 400 Bad Request\r\n\r\nMissing parameters");
                out.flush();
                return;
            }
*/
