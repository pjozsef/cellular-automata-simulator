package hu.elte.inf.people.pojsaai.cellularautomaton.neighborhood;

/**
 * An exception thrown when invalid initialization occours.
 * @author József Pollák
 */
public class InvalidInitializatorException  extends CellularAutomatonException{
    public InvalidInitializatorException(){
    }

    public InvalidInitializatorException(String message){
        super(message);
    }  

    public InvalidInitializatorException(String message, Throwable cause) {
        super(message, cause);
    }
    
    
}
