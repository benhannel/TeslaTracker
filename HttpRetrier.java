package app;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ben Hannel
 */
public class HttpRetrier<V> implements HttpCallable<V> {

    public static final int DEFAULT_NUMBER_OF_RETRIES = 5;
    public static final long DEFAULT_WAIT_TIME = 1000;
    private int numberOfRetries; //total number of tries
    private long timeToWait; //wait interval, exponential backoff is added later
    private HttpCallable<V> task;

    public HttpRetrier(HttpCallable<V> task) {
        this(DEFAULT_NUMBER_OF_RETRIES, DEFAULT_WAIT_TIME, task);
    }

    public HttpRetrier(int numberOfRetries, long timeToWait, HttpCallable<V> task) {
        this.numberOfRetries = numberOfRetries;
        this.timeToWait = timeToWait;
        this.task = task;
    }

    public V call() throws IOException {
        IOException last = null;
        for (int attempt = 1; attempt <= numberOfRetries; attempt++) {
            if (Thread.interrupted())
                throw new IOException("Thread interruption forced");

            try {
                return task.call();
            } catch (MalformedURLException e) {
                throw e;
            } catch (IOException e) {
                last = e;
                System.err.println("Attempt " + attempt + " failed: " + e.getLocalizedMessage());
                e.printStackTrace();
                try {
                    Thread.sleep((int) (timeToWait * Math.random() * (attempt + 1)));
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                    throw new IOException("Thread sleep was interrupted");
                }
            }
        }
        throw new IOException(numberOfRetries
                + " attempts to retry failed at " + timeToWait
                + "ms interval", last);
    }
}