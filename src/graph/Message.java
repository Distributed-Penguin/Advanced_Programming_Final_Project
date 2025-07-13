package graph;

import java.util.Date;

public class Message {
    public final byte[] data;
    public final String asText;
    public final double asDouble;
    public final Date date;
    
    //constructor given string type message 
    public Message(String s) {
    	double temp = Double.NaN;
    	data = s.getBytes();
    	asText = s;
    	try {
			temp = Double.parseDouble(s);
		} catch (NumberFormatException e) {}
    	asDouble = temp;
    	date = new Date();
    }
    
  //constructor given double type message
    public Message(double d) {
    	this(Double.toString(d));
    }
    
  //constructor given byte[] type message
    public Message(byte[] data) {
    	this(new String(data));
    }

}
