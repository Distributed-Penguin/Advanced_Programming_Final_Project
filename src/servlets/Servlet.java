package servlets;

import server.RequestParser.RequestInfo;
import java.io.IOException;
import java.io.OutputStream;
/**
 * servlet interface for MyHTTPServer use
 */
public interface Servlet {
    void handle(RequestInfo ri, OutputStream toClient) throws IOException; //HTTP request handler
    void close() throws IOException; //close servlet
}
