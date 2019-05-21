package hu.elte.inf.people.pojsaai.evaluator.common.token;

import hu.elte.inf.people.pojsaai.evaluator.TokenVisitor;
import lombok.experimental.Value;

/**
 * A number token that can represent any double value.
 * @author József Pollák
 */
@Value
public class NumberToken implements Token{
    /**
     * The numeric value represented by this token.
     */
    public final Double numValue;

    public NumberToken(Double numValue){
        this.numValue=numValue;
    }
    
    @Override
    public String toString(){
        return super.toString() + " Value: "+numValue;
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
