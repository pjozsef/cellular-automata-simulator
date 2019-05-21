package hu.elte.inf.people.pojsaai.cellularautomaton.data;

import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

/**
 *
 * @author József Pollák
 */
public class CAInitializerTest {

    @Test
    public void testCreate_useCAExecutor() throws IOException, URISyntaxException {
        CAInitializer expected = getTestInitializer();
        String jsonPath = "/jsonInput01.json";
        String s = getClass().getResource(jsonPath).getPath();
        CAInitializer init = CAInitializer.create(new File(s));

        assertThat(init)
                .isEqualTo(expected);
    }

    @Test
    public void testCreate_useCustomExecutor() throws IOException, URISyntaxException {
        CAInitializer expected = getTestInitializer();
        String jsonPath = "/jsonInput02.json";
        String s = getClass().getResource(jsonPath).getPath();
        CAInitializer init = CAInitializer.create(new File(s));

        assertThat(init)
                .isEqualTo(expected);
    }

    @Test
    public void test_equals() throws IOException {
        String jsonPath = "/jsonInput02.json";
        String s = getClass().getResource(jsonPath).getPath();
        CAInitializer init1 = CAInitializer.create(new File(s));
        CAInitializer init2 = CAInitializer.create(new File(s));

        CAInitializer expected = getTestInitializer();

        assertThat(init1)
                .isEqualTo(init2);
        assertThat(init1)
                .isEqualTo(expected);
        assertThat(init2)
                .isEqualTo(expected);
    }

    private CAInitializer
            getTestInitializer() throws JsonMappingException {
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

        long seed = 12124124L;

        return new CAInitializer(cells, transitionTable, seed);
    }
}
