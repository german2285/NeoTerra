package neoterra.concurrent.cache;

public interface ExpiringEntry {
    long getTimestamp();
    
    default void close() {
    }
}
