package hu.elte.inf.people.pojsaai.evaluator.exception;

/**
 * An exception thrown when a problem occours during parsing.
 * @author József Pollák
 */
public class EvaluationException extends RuntimeException{

    public EvaluationException(String message){
        super(message);
    }

    public EvaluationException(String message, Throwable cause){
        super(message, cause);
    }
    
}
