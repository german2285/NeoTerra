package neoterra.concurrent.cache;

public interface SafeCloseable extends AutoCloseable {
    void close();
}
