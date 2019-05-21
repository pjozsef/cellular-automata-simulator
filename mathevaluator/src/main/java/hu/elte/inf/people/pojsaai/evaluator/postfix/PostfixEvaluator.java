package hu.elte.inf.people.pojsaai.evaluator.postfix;

import hu.elte.inf.people.pojsaai.evaluator.TokenVisitor;
import hu.elte.inf.people.pojsaai.evaluator.common.token.LeftParenthesisToken;
import hu.elte.inf.people.pojsaai.evaluator.exception.EvaluationException;
import hu.elte.inf.people.pojsaai.evaluator.common.token.NumFunctionToken;
import hu.elte.inf.people.pojsaai.evaluator.common.token.NumberToken;
import hu.elte.inf.people.pojsaai.evaluator.common.token.RightParenthesisToken;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * This class is responsible for evaluating a list of tokens in postfix form 
 * as a numeric value. This class implements the TokenVisitor interface, 
 * so the evaluation steps are split into multiple methods, 
 * each accepting a certain kind of implementation of Token.
 * @author József Pollák
 */
public class PostfixEvaluator implements TokenVisitor {

    private final Deque<NumberToken> stack;
    private double result;
    private boolean closed;

    public PostfixEvaluator() {
        this.stack = new ArrayDeque<>();
        this.closed = false;
    }

    /**
     * {@inheritDoc }
     * @param function 
     */
    @Override
    public void visit(NumFunctionToken function) {
        ensureNotClosed();
        NumberToken num2, num1;
        num2 = getStackTop(function);
        num1 = getStackTop(function);

        double resultValue = function.apply(num1.numValue, num2.numValue);
        stack.push(new NumberToken(resultValue));
    }

    /**
     * {@inheritDoc }
     * @param t 
     */
    @Override
    public void visit(NumberToken t) {
        ensureNotClosed();
        stack.push(t);
    }

    /**
     * {@inheritDoc }
     * @param t 
     */
    @Override
    public void visit(LeftParenthesisToken t) {
        ensureNotClosed();
        throw new EvaluationException("Left parentheses in a postfix token list are not permitted.");
    }

    /**
     * {@inheritDoc }
     * @param t 
     */
    @Override
    public void visit(RightParenthesisToken t) {
        ensureNotClosed();
        throw new EvaluationException("Right parentheses in a postfix token list are not permitted.");
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
     * Closes this evaluator.Closing means this evaluator will not accept any more Token as input.
     * @throws IllegalArgumentException if this instance is already closed.
     * @throws EvaluationException if any syntax errors are found in the Token sequence.
     */
    public void close(){
        ensureNotClosed();
        if(stack.size() != 1){//the result is the only value at the end
            throw new EvaluationException("Stack not empty after evaluation! \nRemained in stack:" + stack);
        }
        result = stack.pop().numValue;
        closed = true;
    }
    
    /**
     * Returns the numeric value that equals to the evaluation of the input Token sequence.
     * @return the numeric value of the expression
     */
    public Double getResult(){
        ensureClosed();
        return result;
    }
    
    private void ensureNotClosed() {
        if (closed) {
            throw new IllegalStateException("Invalid method call. The evaluator is already closed.");
        }
    }

    private void ensureClosed() {
        if (!closed) {
            throw new IllegalStateException("Invalid method call. The evaluator must be closed first.");
        }
    }
    
    private NumberToken getStackTop(NumFunctionToken function) {
        if (!stack.isEmpty()) {
            return stack.pop();
        }
        throw new EvaluationException("Operand missing at: " + function);
    }

}
