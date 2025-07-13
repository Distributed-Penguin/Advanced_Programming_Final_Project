package views;

import server.*;
import servlets.*;

public class Main {
	//server init
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
