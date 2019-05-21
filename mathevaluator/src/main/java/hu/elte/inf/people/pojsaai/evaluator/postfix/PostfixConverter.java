package hu.elte.inf.people.pojsaai.evaluator.postfix;

import hu.elte.inf.people.pojsaai.evaluator.TokenVisitor;
import hu.elte.inf.people.pojsaai.evaluator.common.token.LeftParenthesisToken;
import hu.elte.inf.people.pojsaai.evaluator.common.token.NumFunctionToken;
import hu.elte.inf.people.pojsaai.evaluator.common.token.NumberToken;
import hu.elte.inf.people.pojsaai.evaluator.common.token.RightParenthesisToken;
import hu.elte.inf.people.pojsaai.evaluator.common.token.Token;
import hu.elte.inf.people.pojsaai.evaluator.exception.EvaluationException;
import java.util.Deque;
import java.util.List;
import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * This class is responsible for converting a list of tokens from 
 * infix to postfix form. This class implements the TokenVisitor interface, 
 * so the conversion steps are split into multiple methods, each accepting a 
 * certain kind of implementation of Token.
 * @author József Pollák
 */
public class PostfixConverter implements TokenVisitor {

    private final Deque<Token> stack;
    private final List<Token> result;
    private boolean closed;

    public PostfixConverter() {
        this.stack = new ArrayDeque<>();
        this.result = new ArrayList<>();
        closed = false;
    }

    /**
     * {@inheritDoc }
     * @param t 
     */
    @Override
    public void visit(NumFunctionToken t) {
        ensureNotClosed();
        Token top = stack.peek();
        if (stack.isEmpty() || leftParenthesisOnTop() || lowerPriorityFunctionOnTop(t)) {
            stack.push(t);
        } else {
            while (!stack.isEmpty() && top.getClass() == NumFunctionToken.class && GreaterOrEqualPriorityFunctionOnTop(t)) {
                result.add(stack.pop());
                top = stack.peek();
            }
            stack.push(t);
        }
    }

    /**
     * {@inheritDoc }
     * @param t 
     */
    @Override
    public void visit(NumberToken t) {
        ensureNotClosed();
        result.add(t);
    }

    /**
     * {@inheritDoc }
     * @param t 
     */
    @Override
    public void visit(LeftParenthesisToken t) {
        ensureNotClosed();
        stack.push(t);
    }

    /**
     * {@inheritDoc }
     * @param t 
     */
    @Override
    public void visit(RightParenthesisToken t) {
        ensureNotClosed();
        while (!leftParenthesisOnTop()) {
            result.add(stack.pop());
            if (stack.peek() == null) {
                throw new EvaluationException("Left parenthesis missing!");
            }
        }
        stack.pop();
    }
    
    /**
     * {@inheritDoc }
     * @param t 
     */
    @Override
    public void visit(Object t){
        throw new EvaluationException("Unrecognized object: " + t);
    }

    
    /**
     * Closes this converter, and prepares the result. 
     * Closing means this converter will not accept any more Token as input.
     * @throws IllegalArgumentException if this instance is already closed.
     * @throws EvaluationException if any syntax errors are found in the Token sequence.
     */
    public void close() {
        ensureNotClosed();
        while (!stack.isEmpty() && numFunctionOnTop()) {
            result.add(stack.pop());
        }

        if (!stack.isEmpty()) {
            throw new EvaluationException("Right parenthesis missing!");
        }
        closed = true;
    }

    /**
     * Returns the postfix form of the Token sequences visited.
     * @return the postfix Token list
     * @throws IllegalArgumentException if this converter is not yet closed
     */
    public List<Token> getPostfixForm() {
        ensureClosed();
        return result;
    }

    private boolean leftParenthesisOnTop() {
        return stack.peek().getClass() == LeftParenthesisToken.class;
    }

    private boolean numFunctionOnTop() {
        return stack.peek().getClass() == NumFunctionToken.class;
    }

    private boolean lowerPriorityFunctionOnTop(NumFunctionToken function) {
        Token top = stack.peek();
        return top.getClass() == NumFunctionToken.class && ((NumFunctionToken) top).priority < function.priority;
    }

    private boolean GreaterOrEqualPriorityFunctionOnTop(NumFunctionToken function) {
        Token top = stack.peek();
        return ((NumFunctionToken) top).priority >= function.priority;
    }

    private void ensureNotClosed() {
        if (closed) {
            throw new IllegalStateException("Invalid method call. The converter is already closed.");
        }
    }

    private void ensureClosed() {
        if (!closed) {
            throw new IllegalStateException("Invalid method call. The converter must be closed first.");
        }
    }
}
