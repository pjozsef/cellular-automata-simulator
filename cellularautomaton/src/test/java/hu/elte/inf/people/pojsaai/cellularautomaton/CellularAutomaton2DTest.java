package hu.elte.inf.people.pojsaai.cellularautomaton;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.elte.inf.people.pojsaai.cellularautomaton.data.CAInitializer;
import hu.elte.inf.people.pojsaai.cellularautomaton.data.Rule;
import hu.elte.inf.people.pojsaai.cellularautomaton.jobs.CAExecutor;
import hu.elte.inf.people.pojsaai.evaluator.exception.EvaluationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import static org.assertj.core.api.Assertions.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author József Pollák
 */
public class CellularAutomaton2DTest {

    private int[][] cells1;

    @Before
    public void setUp() {
        cells1 = new int[][]{
            {1, 1, 0, 1, 0},
            {0, 1, 0, 0, 0},
            {1, 1, 0, 1, 0},
            {0, 1, 0, 1, 0},
            {1, 1, 0, 0, 1}
        };
    }

    @Test
    public void test_ifUsesCAExecutorPool() throws JsonMappingException {
        CAInitializer init = new CAInitializer(cells1, getGameOfLifeRules(), 0);
        CellularAutomaton2D ca = new CellularAutomaton2D(init);
        assertThat(CAExecutor.getInstance()).isSameAs(ca.getExecutor());
    }

    @Test
    public void test_initFromJSON() throws IOException {
        int[][] cells = new int[][]{{1, 0, 1, 1}, {0, 0, 0, 0}, {0, 1, 1, 0}, {1, 1, 0, 1}};
        Rule rule0 = Rule
                .builder()
                .expressions(new ArrayList<>(Arrays.asList("SQ(1):3<1")))
                .nextState(1)
                .build();

        Rule rule1 = Rule
                .builder()
                .expressions(new ArrayList<>(Arrays.asList("D(0):1+2==3")))
                .nextState(0)
                .build();
        Map<Integer, List<Rule>> transitionTable = new HashMap<>();
        transitionTable.put(0, Arrays.asList(rule0));
        transitionTable.put(1, Arrays.asList(rule1));

        final long seed = 12124124;

        String inputJSON
                = "{\"cells\":[[1,0,1,1],[0,0,0,0],[0,1,1,0],[1,1,0,1]],"
                + "\"rules\":{\"0\":[{\"expressions\":[\"SQ(1):3<1\"],\"nextState\":1}],\"1\":[{\"expressions\":[\"D(0):1+2==3\"],\"nextState\":0}]},"
                + "\"seed\":12124124}";

        ObjectMapper mapper = new ObjectMapper();
        CAInitializer init = mapper.readValue(inputJSON, CAInitializer.class);
        CellularAutomaton2D[] ca = {new CellularAutomaton2D(init)};

        assertThat(ca)
                .extracting("executor", "initializer")
                .contains(tuple(
                                CAExecutor.getInstance(),
                                new CAInitializer(cells, transitionTable, seed)));
    }

    @Test
    public void test_SeedSettings() throws EvaluationException {
        final int[][] cells1 = {
            {1, 1, 0, 1, 0},
            {0, 1, 0, 0, 0},
            {1, 1, 0, 1, 0},
            {0, 1, 0, 1, 0},
            {1, 1, 0, 0, 1}
        };

        int[][] cells2 = {
            {1, 1, 0, 1, 0},
            {0, 1, 0, 0, 0},
            {1, 1, 0, 1, 0},
            {0, 1, 0, 1, 0},
            {1, 1, 0, 0, 1}
        };

        Map<Integer, List<Rule>> rules = new HashMap<>();

        List<Rule> rules1 = new ArrayList<>();
        List<String> expressions1 = new ArrayList<>();
        expressions1.add("D(2):1+3<6");
        expressions1.add("SQ(1):0+D(2):1>2");
        Rule r1 = new Rule(expressions1, 1);
        rules1.add(r1);

        List<Rule> rules2 = new ArrayList<>();
        List<String> expressions2 = new ArrayList<>();
        expressions2.add("SQ(2):1>8");
        Rule r2 = new Rule(expressions2, 0);
        rules2.add(r2);

        rules.put(0, rules1);
        rules.put(1, rules2);

        final long seed = 400;

        CellularAutomaton2D ca1 = new CellularAutomaton2D(new CAInitializer(cells1, rules, seed));
        CellularAutomaton2D ca2 = new CellularAutomaton2D(new CAInitializer(cells2, rules, seed));

        int loopLimit = 10;
        for (int i = 0; i < loopLimit; ++i) {
            ca1.nextState();
            ca2.nextState();
        }

        List<int[][]> cache1 = ca1.getCellCache();
        List<int[][]> cache2 = ca2.getCellCache();
        IntStream.range(0, cache1.size())
                .forEach((i) -> {
                    assertThat(cache1.get(i)).isEqualTo(cache2.get(i));
                });
    }

    @Test
    public void test_countNumberOfStates_2_states() {
        assertThat(CellularAutomaton2D.countNumberOfStatesIn(cells1))
                .isEqualTo(2);
    }

    @Test
    public void test_countNumberOfStates_5_states() {
        int[][] cells = {
            {1, 2, 5, 4, 3},
            {5, 2, 3, 1, 1},
            {1, 1, 1, 1, 1},
            {2, 2, 1, 2, 1},
            {5, 3, 2, 4, 3}};
        assertThat(CellularAutomaton2D.countNumberOfStatesIn(cells))
                .isEqualTo(5);
    }

    @Test
    public void test_countNumberOfStates_0_states() {
        assertThat(CellularAutomaton2D.countNumberOfStatesIn(new int[][]{}))
                .isEqualTo(0);
    }

    @Test
    public void test_cellStateTransitionCorrectness() throws EvaluationException {
        int[][] cells = {
            {1, 1, 0},
            {0, 0, 0},
            {1, 0, 0}
        };

        CellularAutomaton2D ca = new CellularAutomaton2D(new CAInitializer(cells, getGameOfLifeRules(), 0));
        ca.nextState();
        ca.nextState();
        ca.nextState();
        List<int[][]> cache = ca.getCellCache();

        List<int[][]> expected = new ArrayList<>();
        expected.add(cells);
        expected.add(new int[][]{
            {1, 1, 1},
            {1, 1, 1},
            {1, 1, 1}
        });
        expected.add(new int[][]{
            {0, 0, 0},
            {0, 0, 0},
            {0, 0, 0}
        });
        expected.add(new int[][]{
            {0, 0, 0},
            {0, 0, 0},
            {0, 0, 0}
        });

        IntStream.range(0, expected.size())
                .forEach((i) -> {
                    assertThat(cache.get(i))
                    .isEqualTo(expected.get(i));
                });
    }
    
    @Test
    public void test_defensiveMatrixCopy(){
        int[][] cells = {
            {1, 1, 0},
            {0, 0, 0},
            {1, 0, 0}
        };
        
        
    }

    private Map<Integer, List<Rule>> getGameOfLifeRules() {
        Map<Integer, List<Rule>> caRules = new HashMap<>();

        List<Rule> zeroStateRules = new ArrayList<>();
        List<String> zeroExpressions = new ArrayList<>();
        zeroExpressions.add("SQ(1):1==3");
        Rule zeroToOne = new Rule(zeroExpressions, 1);
        zeroStateRules.add(zeroToOne);

        caRules.put(0, zeroStateRules);

        List<Rule> firstStateRules = new ArrayList<>();
        List<String> firstExpressions = new ArrayList<>();
        firstExpressions.add("SQ(1):1>3");
        firstExpressions.add("SQ(1):1<2");
        Rule oneToZero = new Rule(firstExpressions, 0);
        firstStateRules.add(oneToZero);

        caRules.put(1, firstStateRules);
        return caRules;
    }
}
