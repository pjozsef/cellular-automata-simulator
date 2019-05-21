package hu.elte.inf.people.pojsaai.evaluator.common.token;

import hu.elte.inf.people.pojsaai.evaluator.TokenVisitor;

/**
 * A singleton class, implementing the Token interface and representing 
 * a left parenthesis in the mathematical expression.
 * @author József Pollák
 */
public class LeftParenthesisToken implements Token {

    private static LeftParenthesisToken instance;

    private LeftParenthesisToken() {
    }

    /**
     * Getter to the singleton instance
     * @return the LeftParenthesisToken instance
     */
    public static LeftParenthesisToken getInstance() {
        if (instance == null) {
            instance = new LeftParenthesisToken();
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
