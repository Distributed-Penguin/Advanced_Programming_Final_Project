package configs;

import graph.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class GenericConfig implements Config {
    private final List<Agent> agents = new ArrayList<>();
    private String confFile;
    private int version = 0;

    //set config filename in local variable
    public void setConfFile(String filename) {
        this.confFile = filename;
    }
    
    //create graph from config file
    @Override
    public void create() {
        try (BufferedReader reader = new BufferedReader(new FileReader(confFile))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
            	if (!line.trim().isEmpty()) {
            	    lines.add(line.trim());
            	}
            }
            if (lines.size() % 3 != 0) {
                throw new RuntimeException("Invalid configuration file format.");
            }

            for (int i = 0; i < lines.size(); i += 3) {
                
            	String className = lines.get(i);
                
                String[] subs = lines.get(i + 1).split(",");
                String[] pubs = lines.get(i + 2).split(",");

                Class<?> cls = Class.forName(className);
                Constructor<?> ctor = cls.getConstructor(String[].class, String[].class);
                Agent agent = (Agent) ctor.newInstance((Object) subs, (Object) pubs);
                
                ParallelAgent pa = new ParallelAgent(agent, 100);
                agents.add(pa);
            }
            version++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //returns agent name
    @Override
    public String getName() {
        return "GenericConfig";
    }
    
    //returns version number
    @Override
    public int getVersion() {
        return version;
    }
    
    //closes all agents and itself
    @Override
    public void close() {
        for (Agent agent : agents) {
            agent.close();
        }
        agents.clear();
    }
}
