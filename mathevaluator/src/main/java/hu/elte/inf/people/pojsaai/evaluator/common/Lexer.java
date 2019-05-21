package hu.elte.inf.people.pojsaai.evaluator.common;

import hu.elte.inf.people.pojsaai.evaluator.exception.EvaluationException;
import hu.elte.inf.people.pojsaai.evaluator.common.token.NumberToken;
import hu.elte.inf.people.pojsaai.evaluator.common.token.Token;
import hu.elte.inf.people.pojsaai.evaluator.common.token.TokenFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The Lexer class is used for tokenizing a string input.
 *
 * @author József Pollák
 */
public class Lexer {

    private static final String[] captureGroups;
    private static final String regex;

    static {
        captureGroups = new String[]{
            "(?<number>-?\\d+(\\.\\d+)?)",
            "(?<plus>\\+)",
            "(?<minus>-)",
            "(?<multiply>\\*)",
            "(?<divide>/)",
            "(?<modulo>%)",
            "(?<leftParenthesis>\\()",
            "(?<rightParenthesis>\\))",
            "(?<notEqual>!=)",
            "(?<greaterOrEqual>\\>=)",
            "(?<lessOrEqual>\\<=)",
            "(?<equal>==)",
            "(?<less>\\<)",
            "(?<greater>\\>)",
            "(?<anythingElse>.+)"
        };

        regex = Arrays.stream(captureGroups).collect(Collectors.joining("|"));
    }

    private Lexer() {
    }

    /**
     * Tokenizes the String input.
     *
     * @param input The input expression in String format
     * @return the list of Tokens
     * @throws EvaluationException if an invalid character is found in the input
     */
    public static List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>(20);
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        while (m.find()) {
            if (m.group("anythingElse") != null) {
                throw new EvaluationException("Invalid token: " + m.group());
            }
            tokens.add(TokenFactory.create(m.group()));
        }
        insertPlusBeforeNegativeNumber(tokens);
        return tokens;
    }

    private static List<Token> insertPlusBeforeNegativeNumber(List<Token> tokens) {
        for (int i = 1; i < tokens.size(); ++i) {
            Token prev = tokens.get(i - 1);
            Token curr = tokens.get(i);
            Class number = NumberToken.class;
            if (prev.getClass() == number && curr.getClass() == number && ((NumberToken) curr).numValue < 0) {
                tokens.add(i, TokenFactory.create("+"));
            }
        }
        return tokens;
    }
}
