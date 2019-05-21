package hu.elte.inf.people.pojsaai.cellularautomatasimulator.cellularautomaton;

import hu.elte.inf.people.pojsaai.cellularautomaton.neighborhood.InvalidInitializatorException;
import hu.elte.inf.people.pojsaai.cellularautomaton.neighborhood.Neighborhood;
import hu.elte.inf.people.pojsaai.evaluator.Evaluator;
import hu.elte.inf.people.pojsaai.evaluator.exception.EvaluationException;
import hu.elte.inf.people.pojsaai.evaluator.postfix.PostfixFacade;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

/**
 * This class validates the transition rules given by the user.
 * @author József Pollák
 */
public final class RuleValidator {

    private static final DropShadow errorEffect = new DropShadow(BlurType.ONE_PASS_BOX, Color.RED, 10, 0.5, 0, 0);

    private RuleValidator() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the error indicating red border effect object.
     * @return the error indicating red border effect object.
     */
    public static DropShadow getErrorEffect() {
        return errorEffect;
    }

    /**
     * Checks if the transitionrules are written in the correct syntactic form.
     * @param rules the transition rules
     * @param stateCount the count of states
     * @return true if the rules are written correctly, false otherwise
     */
    public static boolean isInputCorrect(final String rules, final int stateCount) {
        Evaluator evaluator = new PostfixFacade();
        SimpleBooleanProperty result = new SimpleBooleanProperty(true);
        Arrays.stream(
                rules.split("\n"))
                .map(text -> text.replaceAll(" ", ""))
                .filter((text) -> !"".equals(text))
                .forEach(
                        (text) -> {
                            if ("ALWAYS".equals(text) || "TRUE".equals(text)) {
                                result.set(true);
                            } else {
                                if (!hasValidStateReference(text, stateCount)) {
                                    result.set(false);
                                }
                                String dummyReplace = text.replaceAll(Neighborhood.NEIGHBORHOOD_CODES_REGEX + "\\(\\d+\\):\\d+", "1");//replace SQ(3):2 with constant 1
                                try {
                                    evaluator.evaluateRelation(dummyReplace);
                                } catch (EvaluationException ex) {
                                    result.set(false);
                                }
                            }
                        }
                );
        return result.get();
    }

    /**
     * Checks if the transition rule references valid states.
     * @param input the input rule
     * @param stateCount the count of states
     * @return true if only existant states are referenced, otherwise false
     */
    public static boolean hasValidStateReference(final String input, final int stateCount) {
        Pattern p = Pattern.compile(":\\d+");
        Matcher m = p.matcher(input);
        while (m.find()) {
            int referencedState = Integer.parseInt(m.group().substring(1));
            if (referencedState >= stateCount) {
                return false;
            }
        }
        return true;
    }

    /**
     * Traverses the Node structure and looks for Nodes that have the error effect on them.
     * @param node the root node
     * @throws InvalidInitializatorException if a node is found that has the error effect on it
     */
    public static void checkForRuleErrors(Node node) throws InvalidInitializatorException {
        if (node instanceof Parent) {
            Parent parent = (Parent) node;
            for (Node child : parent.getChildrenUnmodifiable()) {
                checkForRuleErrors(child);
            }
        } else {
            if (node.getEffect() == errorEffect) {
                throw new InvalidInitializatorException("Cannot proceed, since there are invalid rules!");
            }
        }
    }
}
