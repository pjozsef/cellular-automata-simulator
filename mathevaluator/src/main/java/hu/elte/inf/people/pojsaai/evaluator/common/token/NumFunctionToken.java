package hu.elte.inf.people.pojsaai.evaluator.common.token;

import hu.elte.inf.people.pojsaai.evaluator.TokenVisitor;
import java.util.function.BiFunction;
import lombok.experimental.Value;

/**
 * A numerical function token.
 * Can represent any binary function that returns a number value.
 * @author József Pollák
 */
@Value
public class NumFunctionToken implements Token{
    private final String functionType;
    private final BiFunction<Double, Double, Double> function;
    public final int priority;

    public NumFunctionToken(String functionType, BiFunction<Double, Double, Double> function, int priority){
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
    public Double apply(Double a, Double b){
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
