package hu.elte.inf.people.pojsaai.evaluator;

import hu.elte.inf.people.pojsaai.evaluator.exception.EvaluationException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @author József Pollák
 */
public class JSEvaluator implements Evaluator {

    private static final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

    public JSEvaluator() {
    }

    @Override
    public double evaluateExpression(String expression) {
        try {
            Object result = engine.eval("Number(" + expression + ")");
            if (result instanceof Double) {
                return (Double) result;
            }
        } catch (ScriptException ex) {
            throw new EvaluationException("The testing JSEvaluator encountered an exception.", ex);
        }
        throw new EvaluationException("The result of evaluateExpression in JSEvaluator is not a Double.");
    }

    @Override
    public boolean evaluateRelation(String expression) {
        try {
            Object result = engine.eval(expression);
            if (result.getClass().equals(Boolean.class)) {
                return (Boolean) result;
            }
        } catch (ScriptException ex) {
            Logger.getLogger(JSEvaluator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

}
