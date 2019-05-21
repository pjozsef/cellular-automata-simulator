package hu.elte.inf.people.pojsaai.evaluator;

import hu.elte.inf.people.pojsaai.evaluator.common.token.LeftParenthesisToken;
import hu.elte.inf.people.pojsaai.evaluator.common.token.NumFunctionToken;
import hu.elte.inf.people.pojsaai.evaluator.common.token.NumberToken;
import hu.elte.inf.people.pojsaai.evaluator.common.token.RightParenthesisToken;

/**
 * This interface should be implemented by any class 
 * whose instances are to be used to implement the 
 * Visitor pattern on the implementations of the {@link hu.elte.inf.people.pojsaai.evaluator.common.token.Token} class.
 * @author József Pollák
 */
public interface TokenVisitor {
    /**
     * Handler method for {@link NumFunctionToken}s.
     * @param t the Token instance to visit
     */
    public void visit(NumFunctionToken t);
    
    /**
     * Handler method for {@link NumberToken}s.
     * @param t the Token instance to visit
     */
    public void visit(NumberToken t);
    
    /**
     * Handler method for {@link LeftParenthesisToken}s.
     * @param t the Token instance to visit
     */
    public void visit(LeftParenthesisToken t);
    
    /**
     * Handler method for {@link RightParenthesisToken}s.
     * @param t the Token instance to visit
     */
    public void visit(RightParenthesisToken t);
    
    /**
     * Handler method for {@link Object}s.
     * This method handles invokes with unsupported parameter type.
     * @param t the Token instance to visit
     */
    public void visit(Object t);
}
