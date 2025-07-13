package servlets;

import server.RequestParser.RequestInfo;

import java.io.*;
import java.nio.file.*;

public class HtmlLoader implements Servlet {

    private final String directory;

    public HtmlLoader(String directory) {
        this.directory = directory;
    }
    
    //handle HTTP GET "/app" requests - loads index page
    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
        PrintWriter out = new PrintWriter(toClient);

        try {
            String uri = ri.getUri(); // e.g., /app/index.html
            String filename = uri.substring(uri.lastIndexOf("/") + 1); // get "index.html"
            Path filePath = Paths.get(directory, filename);

            if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
                out.println("HTTP/1.1 404 Not Found\r\n\r\nFile not found");
                out.flush();
                return;
            }

            byte[] content = Files.readAllBytes(filePath);
            out.println("HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n");
            out.flush();
            toClient.write(content);
            toClient.flush();

        } catch (Exception e) {
            out.println("HTTP/1.1 500 Internal Server Error\r\n\r\n");
            out.flush();
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {}
}