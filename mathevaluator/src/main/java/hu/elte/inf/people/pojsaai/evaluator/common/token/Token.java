package hu.elte.inf.people.pojsaai.evaluator.common.token;

import hu.elte.inf.people.pojsaai.evaluator.TokenVisitor;

/**
 * This interface should be implemented by any class 
 * whose instances are to be used as tokens for a mathematical expression.
 * @author József Pollák
 */
public interface Token {
    /**
     * Accepts a TokenVisitor object.
     * By contract, this method must call the visit method of the input parameter.
     * @param visitor the object that visits this Token
     */
    public void accept(TokenVisitor visitor);
}
