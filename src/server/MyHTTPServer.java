package server;

import servlets.Servlet;
import server.RequestParser.RequestInfo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * this class runs an HTTP server with designated servlets to process specific client requests
 */
public class MyHTTPServer extends Thread implements HTTPServer{
    
	private final int port;
    private final ExecutorService threadPool;
    private final Map<String, Map<String, Servlet>> servletMap;
    private volatile boolean running;
    
    //constructor
    public MyHTTPServer(int port,int nThreads){
    	this.port = port;
        this.threadPool = Executors.newFixedThreadPool(nThreads);
        this.servletMap = new ConcurrentHashMap<>();
        
        for (String method : Arrays.asList("GET", "POST", "DELETE")) {
            servletMap.put(method, new ConcurrentHashMap<>());
        }
    }

    public void addServlet(String httpCommand, String uri, Servlet s){
    	httpCommand = httpCommand.toUpperCase(); // normalize key
        servletMap.putIfAbsent(httpCommand, new ConcurrentHashMap<>());
        servletMap.get(httpCommand).put(uri, s);
    }

    public void removeServlet(String httpCommand, String uri){
    	Map<String, Servlet> map = servletMap.get(httpCommand.toUpperCase());
        if (map != null) {
        	map.remove(uri);
        }
    }

    public void run(){
    	running = true;
        try (ServerSocket server = new ServerSocket(port)) {
            server.setSoTimeout(1000);  // allows checking running flag every second
            while (running) {
                try {
                    Socket client = server.accept();
                    threadPool.submit(() -> handleClient(client));
                } catch (SocketTimeoutException ignored) {}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        threadPool.shutdownNow();
        // Close all servlets
        for (Map<String, Servlet> map : servletMap.values()) {
            for (Servlet s : map.values()) {
                try {
                    s.close();
                } catch (IOException ignored) {}
            }
        }
    }
    
    public void close(){
    	running = false;
    }

    
    private void handleClient(Socket client) {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            OutputStream out = client.getOutputStream()
        ) {
        	RequestInfo ri;
            try {
                ri = RequestParser.parseRequest(reader);
            } catch (IOException e) {
                out.write("HTTP/1.1 400 Bad Request\r\n\r\n".getBytes());
                return;
            }
            String method = ri.getHttpCommand().toUpperCase();

            Map<String, Servlet> methodMap = servletMap.getOrDefault(method, Collections.emptyMap());

            // Longest-prefix match
            Servlet selected = null;
            int bestMatchLength = -1;
            for (String key : methodMap.keySet()) {
                if (ri.getUri().startsWith(key) && key.length() > bestMatchLength) {
                    selected = methodMap.get(key);
                    bestMatchLength = key.length();
                }
            }

            if (selected != null) {
                selected.handle(ri, out);
            } else {
                out.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
            }

        } catch (Exception e) {
            try {
                client.getOutputStream().write("HTTP/1.1 500 Internal Server Error\r\n\r\n".getBytes());
            } catch (IOException ignored) {}
            e.printStackTrace();
        }
        try {
			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
