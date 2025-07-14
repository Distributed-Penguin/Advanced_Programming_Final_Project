package server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * helper class for MyHTTPServer to parse HTML requests
 */
public class RequestParser {
	
	/**
	 * inner class containing request info
	 */
    public static RequestInfo parseRequest(BufferedReader reader) throws IOException {        
		
    	String requestLine = reader.readLine(); // e.g., GET /path/to/resource?a=1&b=2 HTTP/1.1
         if (requestLine == null || requestLine.isEmpty()) {
             throw new IOException("Empty request line");
         }
         
         //parse request line
         String[] tokens = requestLine.split(" ");
         if (tokens.length < 2) {
             throw new IOException("Invalid request line: " + requestLine);
         }
         
         //extract path and method
         String method = tokens[0];                  // GET
         String uri = tokens[1];                     // /api/resource?a=1&b=2
         String[] uriSplit = uri.split("\\?", 2);
         String path = uriSplit[0];                  // /api/resource
         String[] uriSegments = Arrays.stream(path.split("/"))
                                      .filter(s -> !s.isEmpty())
                                      .toArray(String[]::new);
         
         // extract query parameters from URI
         Map<String, String> params = new HashMap<>();
         if (uriSplit.length > 1) {
             String query = uriSplit[1];
             for (String pair : query.split("&")) {
                 String[] kv = pair.split("=", 2);
                 if (kv.length == 2) params.put(kv[0], kv[1]);
             }
         }

         String line;
         List<String> headers = new ArrayList<>();

         // read headers
         while ((line = reader.readLine()) != null && !line.isEmpty()) {
             headers.add(line);
         }

         // optional post parameters (e.g., filename=...) after empty line
         while (reader.ready() && (line = reader.readLine()) != null && !line.isEmpty()) {
             String[] kv = line.split("=", 2);
             if (kv.length == 2) params.put(kv[0], kv[1]);
         }

         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         while (reader.ready() && (line = reader.readLine()) != null && !line.isEmpty()) {
             buffer.write(line.getBytes());
             buffer.write('\n');  // add line break back (optional but usually correct)
         }
         byte[] content = buffer.toByteArray();
         
         return new RequestInfo(method, uri, uriSegments, params, content);
    }
	
	// RequestInfo given internal class
    public static class RequestInfo {
        private final String httpCommand;
        private final String uri;
        private final String[] uriSegments;
        private final Map<String, String> parameters;
        private final byte[] content;

        public RequestInfo(String httpCommand, String uri, String[] uriSegments, Map<String, String> parameters, byte[] content) {
            this.httpCommand = httpCommand;
            this.uri = uri;
            this.uriSegments = uriSegments;
            this.parameters = parameters;
            this.content = content;
        }

        public String getHttpCommand() {
            return httpCommand;
        }

        public String getUri() {
            return uri;
        }

        public String[] getUriSegments() {
            return uriSegments;
        }

        public Map<String, String> getParameters() {
            return parameters;
        }

        public byte[] getContent() {
            return content;
        }
    }
}
