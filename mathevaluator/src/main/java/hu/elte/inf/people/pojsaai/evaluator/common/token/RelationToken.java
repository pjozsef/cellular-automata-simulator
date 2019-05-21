package hu.elte.inf.people.pojsaai.evaluator.common.token;

import hu.elte.inf.people.pojsaai.evaluator.TokenVisitor;
import java.util.function.BiFunction;
import lombok.experimental.Value;

/**
 * A relation function token. 
 * Can represent any binary function that compares two numerical input and returns a boolean result.
 * @author József Pollák
 */
@Value
public class RelationToken implements Token{
    private final String functionType;
    private final BiFunction<Double, Double, Boolean> function;
    public final int priority;

    public RelationToken(String functionType, BiFunction<Double, Double, Boolean> function, int priority){
        this.functionType=functionType;
        this.function=function;
        this.priority=priority;
    }
    
    /**
     * Calculates the result of the binary operation.
     * @param a left side of the relation
     * @param b right side of the relation
     * @return the result of the function
     */
    public Boolean apply(Double a, Double b){
        return function.apply(a, b);
    }

    @Override
    public String toString(){
        return super.toString() + " Type: "+functionType;
    }    
    
    /**
     * {@inheritDoc}
     * @param visitor 
     */
    @Override
    public void accept(TokenVisitor visitor) {
        visitor.visit(this);
    }
}
