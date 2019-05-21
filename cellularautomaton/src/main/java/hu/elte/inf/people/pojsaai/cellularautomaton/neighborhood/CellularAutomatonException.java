package hu.elte.inf.people.pojsaai.cellularautomaton.neighborhood;

/**
 * The base exception for all CellularAutomaton based exceptions.
 * @author József Pollák
 */
public class CellularAutomatonException extends Exception{

    public CellularAutomatonException(){
    }

    public CellularAutomatonException(String message){
        super(message);
    }

    public CellularAutomatonException(String message, Throwable cause){
        super(message, cause);
    }
    
}
