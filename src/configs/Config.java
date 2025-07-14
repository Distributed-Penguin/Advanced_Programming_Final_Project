package configs;

/**
 * config class interface
 */
public interface Config {
    void create();
    String getName();
    int getVersion();
    void close();
}
