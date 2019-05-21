package hu.elte.inf.people.pojsaai.cellularautomaton.jobs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * This singleton class is encapsulating an ExecutorService, 
 * which is used by the cellular automaton by default.
 * @author József Pollák
 */
public class CAExecutor {

    private static CAExecutor instance = null;
    private static final ThreadFactory factory = (runnable) -> {
        Thread t = new Thread(runnable);
        t.setName("CAExecutorThread-" + t.getId());
        t.setDaemon(true);
        return t;
    };

    private final ExecutorService pool;

    private CAExecutor() {
        pool = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors(),
                factory);
    }

    /**
     * Returns the encapsulated ExecutorService instance.
     * @return the ExecutorService
     */
    public static synchronized ExecutorService getInstance() {
        if (instance == null) {
            instance = new CAExecutor();
        }
        return instance.pool;
    }
    
    /**
     * Returns the ThreadFactory object that was used to create the Threads for the ExecutorService's Thread pool.
     * @return the ThreadFactory
     */
    public static ThreadFactory getFactory(){
        return factory;
    }
}
