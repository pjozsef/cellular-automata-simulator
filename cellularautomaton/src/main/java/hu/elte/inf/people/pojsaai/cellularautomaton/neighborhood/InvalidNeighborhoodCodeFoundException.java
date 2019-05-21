package hu.elte.inf.people.pojsaai.cellularautomaton.neighborhood;

/**
 * An exception thrown when an invalid neighborhood code is found. Anything is invalid apart from 'SQ', 'D', 'C', 'X', 'ST'
 * @author József Pollák
 */
public class InvalidNeighborhoodCodeFoundException extends CellularAutomatonException{

    public InvalidNeighborhoodCodeFoundException(){
    }

    public InvalidNeighborhoodCodeFoundException(String message){
        super(message);
    }    

    public InvalidNeighborhoodCodeFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    
}
