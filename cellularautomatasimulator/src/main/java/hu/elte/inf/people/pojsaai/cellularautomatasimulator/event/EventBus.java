package hu.elte.inf.people.pojsaai.cellularautomatasimulator.event;

/**
 * The wrapper object arount a Guava EventBus.
 * @author József Pollák
 */
public class EventBus {
    private static final com.google.common.eventbus.EventBus eventBus;
    
    static {
        eventBus = new com.google.common.eventbus.EventBus();
    }
    
    /**
     * Registers an object in the EventBus
     * @param o the object to register
     */
    public static void register(Object o){
        eventBus.register(o);
    }
    
    /**
     * Unregisters an object in the EventBus
     * @param o the object to unregister
     */
    public static void unregister(Object o){
        eventBus.unregister(o);
    }
    
    /**
     * Post a new event.
     * @param o the event
     */
    public static void post(Object o){
        eventBus.post(o);
    }
    
}
