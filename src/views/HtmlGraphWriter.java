package views;

import configs.Graph;
import configs.Node;

import java.io.*;
import java.util.*;
/**
 * This class extracts dynamic graph information from Graph object and injects it into a static HTML file called
 * graph.html, which contains static instructions for graphical representation
 */
public class HtmlGraphWriter {
	//helper function - injects a graph object g into a graph.html graphical template file in cytoscape JS library format
	public static List<String> getGraphHTML(Graph g) {
	    List<String> html = new ArrayList<>();
	    StringBuilder elements = new StringBuilder();
	    elements.append("elements: [\n");
	    
	    //compile vertices set in cytoscape JS library format
	    Set<String> added = new HashSet<>();

	    for (Node node : g) {
	        String id = node.getName();
	        String type = id.startsWith("T") ? "topic" : "agent";
	        id = cleanLabel(id);
	        if (added.add(id)) {
	            elements.append("  { data: { id: '").append(id).append("', type: '").append(type).append("' } },\n");
	        }
	        //For safe programming, for each node added - add neighbors directly (redundancy - all neighbors should be in g)
	        for (Node neighbor : node.getEdges()) {
	            String nid = neighbor.getName();
	            String ntype = nid.startsWith("T") ? "topic" : "agent";
	            nid = cleanLabel(nid);
	            if (added.add(nid)) {
	                elements.append("  { data: { id: '").append(nid).append("', type: '").append(ntype).append("' } },\n");
	            }
	        }
	    }
	    
	  //compile edges set in cytoscape JS library format
	    for (Node node : g) {
	        for (Node neighbor : node.getEdges()) {
	        	String node_id = cleanLabel(node.getName());
	        	String neighbor_id = cleanLabel(neighbor.getName());
	            elements.append("  { data: { source: '").append(node_id)
	                    .append("', target: '").append(neighbor_id).append("' } },\n");
	        }
	    }

	    elements.append("],");

	    // Load template and inject elements
	    File template = new File("html_files/graph.html");
	    try (BufferedReader reader = new BufferedReader(new FileReader(template))) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            if (line.contains("<!--GRAPH_ELEMENTS_PLACEHOLDER-->")) {
	                html.add(elements.toString());
	            } else {
	                html.add(line);
	            }
	        }
	    } catch (IOException e) {
	        html.clear();
	        html.add("<html><body><p>Error loading graph template.</p></body></html>");
	    }

	    return html;
	}
	

    //helper function for clean labels removes 'T','A' prefixes
    private static String cleanLabel(String Label) {
    	return Label.replaceFirst("^[AT]", "");
    }
}