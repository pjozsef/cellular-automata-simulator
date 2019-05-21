package hu.elte.inf.people.pojsaai.evaluator;

/**
 * This interface should be implemented by any class 
 * whose instances are to be used to evaluate string 
 * representations of either numerical or relational 
 * mathematical expressions.
 * 
 * @author József Pollák
 * 
 */

public interface Evaluator{
    /**
     * Evaluates a string input as a numerical mathematical expression, 
     * returning the number value of the expression
     * @param expression the input string
     * @return the double value of the expression
     */
    public double evaluateExpression(String expression);
    
    /**
     * Evaluates a string input as a relational mathematical expression, 
     * returning the boolean value of the expression.
     * @param expression the input string
     * @return the boolean value of the expression
     */
    public boolean evaluateRelation(String expression);
}
