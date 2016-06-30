package app;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 *
 * @author Ben Hannel
 */
public interface HttpCallable<V> extends Callable<V> {

    public V call() throws IOException;
}