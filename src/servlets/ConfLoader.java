package servlets;

import configs.*;
import views.HtmlGraphWriter;
import server.RequestParser.RequestInfo;

import java.io.*;
import java.util.List;

public class ConfLoader implements Servlet {
	
	/**
	 * handle HTTP POST "/upload" requests - generates an HTTP graph representation with HtmlGraphWriter helper class and broadcasts to client
	 */
    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
        PrintWriter out = new PrintWriter(toClient);
        try {
            String content = new String(ri.getContent());
            String fileName = ri.getParameters().getOrDefault("filename", "uploaded.conf");

            // Save uploaded config
            try (FileWriter fw = new FileWriter(fileName)) {
                fw.write(content);
            }

            // Load config and create system
            graph.TopicManagerSingleton.get().clear(); //clean previous config
            GenericConfig gc = new GenericConfig();
            gc.setConfFile(fileName);
            gc.create();
            Graph graph = new Graph();
            graph.createFromTopics();

            // Generate HTML view with assistance of helper function
            List<String> html = HtmlGraphWriter.getGraphHTML(graph);

            // Respond with final HTML
            out.println("HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n");
            html.add("<script>");
            html.add("  const rightFrame = parent.frames['right'];");
            html.add("  if (rightFrame) rightFrame.location.href = '/publish';");
            html.add("</script>");
            for (String line : html) {
                out.println(line);
            }
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
            out.println("HTTP/1.1 500 Internal Server Error\r\n\r\n");
            out.println("<html><body><h2>Error loading configuration.</h2></body></html>");
            out.flush();
        }
    }

    @Override
    public void close() throws IOException {}
}