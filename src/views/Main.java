package views;

import server.*;
import servlets.*;

/**
 * Entry point for the application.
 * Responsible for launching the configured agents and starting the server.
 */
public class Main {
	/**
     * Launches the application from the command line.
     * @param args command-line arguments (not used)
     * @throws Exception if an error occurs during startup
     */
	public static void main(String[] args) throws Exception{
		HTTPServer server=new MyHTTPServer(8080,5);
		
		server.addServlet("GET", "/publish", new TopicDisplayer());
		server.addServlet("POST", "/upload", new ConfLoader());
		server.addServlet("GET", "/app/", new HtmlLoader("html_files"));
		
		server.start();
		
		System.out.println("Server started at http://localhost:8080/app/index.html");
		System.in.read();
		
		server.close();
		System.out.println("done");
	}
}
