package hu.elte.inf.people.pojsaai.evaluator.common.token;

import hu.elte.inf.people.pojsaai.evaluator.TokenVisitor;

/**
 * A singleton class, implementing the Token interface and representing 
 * a right parenthesis in the mathematical expression.
 * @author József Pollák
 */
public class RightParenthesisToken implements Token {
    private static RightParenthesisToken instance;
    
    private RightParenthesisToken() {
    }

    /**
     * Getter to the singleton instance
     * @return the RightParenthesisToken instance
     */
    public static RightParenthesisToken getInstance(){
        if(instance == null){
            instance = new RightParenthesisToken();
        }
        return instance;
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
