package hu.elte.inf.people.pojsaai.evaluator.postfix;

import hu.elte.inf.people.pojsaai.evaluator.Evaluator;
import hu.elte.inf.people.pojsaai.evaluator.common.Lexer;
import hu.elte.inf.people.pojsaai.evaluator.common.token.RelationToken;
import hu.elte.inf.people.pojsaai.evaluator.common.token.Token;
import hu.elte.inf.people.pojsaai.evaluator.common.token.TokenFactory;
import hu.elte.inf.people.pojsaai.evaluator.exception.EvaluationException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An implementation of the Evaluator interface. 
 * This class is also a facade class, serving a simplified and easy to use interface to the underlying Lexer-PostfixConverter-PostfixEvaluator subsystem.
 * @author József Pollák
 */
public class PostfixFacade implements Evaluator {
    private static final String relationPattern = "(==|!=|<=|>=|<|>)";

    /**
     * {@inheritDoc }
     * @param expression
     * @return 
     * @throws EvaluationException if an error is encountered during evaluation
     */
    @Override
    public double evaluateExpression(String expression) {
        List<Token> infixTokens = Lexer.tokenize(removeWhitespaces(expression));

        PostfixConverter converter = new PostfixConverter();
        infixTokens.stream().forEach((t) -> {
            t.accept(converter);
        });
        converter.close();
        List<Token> postfixTokens = converter.getPostfixForm();

        PostfixEvaluator evaluator = new PostfixEvaluator();
        postfixTokens.stream().forEach((t) -> {
            t.accept(evaluator);
        });
        evaluator.close();

        return evaluator.getResult();
    }

    /**
     * {@inheritDoc }
     * @param expression
     * @return 
     * @throws EvaluationException if an error is encountered during evaluation
     */
    @Override
    public boolean evaluateRelation(String expression) {
        Pattern p = Pattern.compile(relationPattern);
        Matcher m = p.matcher(expression);
        List<String> relationOperators = new ArrayList<>();
        while (m.find()) {
            relationOperators.add(m.group());
        }
        validateRelationOperatorCount(relationOperators);

        String relationString = relationOperators.get(0);
        String[] sides = expression.split(relationPattern);
        validateSplitCount(sides);
        RelationToken relation = (RelationToken) TokenFactory.create(relationString);

        return relation.apply(evaluateExpression(sides[0]), evaluateExpression(sides[1]));
    }

    private void validateRelationOperatorCount(List<String> relationOperators) {
        if (relationOperators.isEmpty()) {
            throw new EvaluationException("There was no relation operator found in the input!");
        } else if (relationOperators.size() > 2) {
            throw new EvaluationException("Multiple relation operators were found in the input!");
        }
    }

    private void validateSplitCount(String[] sides) {
        if (sides.length != 2) {
            throw new EvaluationException("One side of the evaluation expression is missing!");
        }
    }

    private String removeWhitespaces(String input) {
        return input.replaceAll(" ", "");
    }
}
