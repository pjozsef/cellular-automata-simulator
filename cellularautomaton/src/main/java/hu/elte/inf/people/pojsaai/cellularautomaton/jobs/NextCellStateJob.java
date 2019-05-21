package hu.elte.inf.people.pojsaai.cellularautomaton.jobs;

import hu.elte.inf.people.pojsaai.cellularautomaton.data.Cell;
import hu.elte.inf.people.pojsaai.cellularautomaton.data.Rule;
import hu.elte.inf.people.pojsaai.cellularautomaton.neighborhood.InvalidNeighborhoodCodeFoundException;
import hu.elte.inf.people.pojsaai.cellularautomaton.neighborhood.Neighborhood;
import hu.elte.inf.people.pojsaai.evaluator.Evaluator;
import hu.elte.inf.people.pojsaai.evaluator.exception.EvaluationException;
import hu.elte.inf.people.pojsaai.evaluator.postfix.PostfixFacade;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import lombok.Value;
import lombok.experimental.Builder;

/**
 * This class represents the required step to calculate 
 * the next state of one cell in a CellularAutomaton2D object.
 * @author József Pollák
 */
@Value
@Builder
public class NextCellStateJob implements Callable<Cell> {

    private final int x, y;
    private final int[][] cells;
    private final List<Rule> transitionRules;
    private final double generatedRandom;

    public NextCellStateJob(int x, int y, int[][] cells, List<Rule> transitionRules, double generatedRandom) {
        this.x = x;
        this.y = y;
        this.cells = cells;
        this.transitionRules = transitionRules;
        this.generatedRandom = generatedRandom;
    }

    /**
     * Calculates the next state of the cell.
     * @return a Cell object containing the next state
     * @throws Exception 
     */
    @Override
    public Cell call() throws Exception {
        Evaluator evaluator = new PostfixFacade();

        List<Rule> possibleTransitionRules = new ArrayList<>();

        for (Rule currentRule : transitionRules) {
            boolean allExpressionsPassed = false;

            for (String expression : currentRule.getExpressions()) {
                try {
                    if ("ALWAYS".equals(expression) || "TRUE".equals(expression)) {
                        allExpressionsPassed = true;
                    } else {
                        allExpressionsPassed = allExpressionsPassed || evaluator.evaluateRelation(Neighborhood.replaceWithNeighborCount(expression, cells, new Point(x, y)));
                    }
                    if (allExpressionsPassed) {
                        break;
                    }
                } catch (InvalidNeighborhoodCodeFoundException ex) {
                    throw new EvaluationException("InvalidNeighborhoodCodeFoundException occoured", ex);
                }
            }
            if (allExpressionsPassed) {
                possibleTransitionRules.add(currentRule);
            }
        }

        int nextState = cells[x][y];
        if (!possibleTransitionRules.isEmpty()) {
            nextState = chooseNextStateFromRules(possibleTransitionRules);
        }
        return new Cell(x, y, nextState);
    }

    private int chooseNextStateFromRules(List<Rule> possibleTransitionRules) {
        int index = 0;
        double increment = 1.0 / possibleTransitionRules.size();
        double d = increment;
        for (; d <= generatedRandom; d += increment) {
            index++;
        }
        return possibleTransitionRules.get(index).getNextState();
    }

}
