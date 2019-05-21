package hu.elte.inf.people.pojsaai.evaluator.postfix;

import hu.elte.inf.people.pojsaai.evaluator.JSEvaluator;
import hu.elte.inf.people.pojsaai.evaluator.exception.EvaluationException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

/**
 *
 * @author József Pollák
 */
public class PostfixFacadeTest {

    private final JSEvaluator je = new JSEvaluator();
    private final PostfixFacade pe = new PostfixFacade();
    private String input;

    @Test
    public void test_evaluate_num_expression_01() {
        String[] inputs = {
            "5*3+10",
            "(-6/9*3)%30+2",
            "6*6*40-(-4*(5/2))",
            "5*5+5*5+(600/6)*-1",
            "5*3+10",
            "5*3+10",
            "5*3+10"};

        Stream.of(inputs).forEach((e) -> {
            assertThat(pe.evaluateExpression(e))
                    .isEqualTo(je.evaluateExpression(e));
        });
    }

    @Test
    public void test_evaluate_relation_01() {
        String[] inputs = {
            "10>8",
            "200+300==500*2-250*2",
            "5*5+5*5+(600/6)*-1==-150",
            "5%5+1>0",
            "5%5+1>=0",
            "5%5+1<=1",
            "5%5+1<2",
            "6*5%5==120%10"};

        Stream.of(inputs).forEach((e) -> {
            assertThat(pe.evaluateRelation(e))
                    .isEqualTo(je.evaluateRelation(e));
        });
    }

    @Test
    public void test_evaluate_relation_with_whitespaces_inserted() {
        String[] inputs = {
            "1 0 >8",
            "200+ 300==500*2 - 250*2",
            "5  *5+5*    5+(600/6)*-1 ==-150",
            "5%5        +1>0",
            " 5%5+1>= 0",
            "5 %5+ 1<=1",
            "5% 5+1< 2",
            "6*5 %5==1 20% 10"};
        List<Boolean> result = Stream.of(inputs)
                .map(pe::evaluateRelation)
                .collect(Collectors.toList());

        List<Boolean> expected = Arrays.asList(new Boolean[]{
            true,
            true,
            false,
            true,
            true,
            true,
            true,
            true
        });

        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void test_double_values_as_input() {
        String[] inputs = {
            "5*3+10+0.53",
            "6*6*40/19",
            "5*5+5*5/3"};
        List<Double> result = Stream.of(inputs)
                .map(pe::evaluateExpression)
                .collect(Collectors.toList());

        Double[] expected = {
            25.53,
            75.789474,
            33.33333
        };
        IntStream.range(0, inputs.length)
                .forEach((i) -> {
                    assertThat(result.get(i))
                    .isEqualTo(expected[i], offset(.0001));
                });
    }

    @Test
    public void test_evaluate_num_with_whitespaces_inserted() {
        String[] inputs = {
            "5*3+1   0+0.5 ",
            "    6*6* 40- (-4*(5/2))",
            "5*5+5 *5+(600/6) *-1",
            "5*3+ 10",
            "5*3+10",
            " 5*3+10"};
        
        List<Double> result = Stream.of(inputs)
                .map(pe::evaluateExpression)
                .collect(Collectors.toList());

        Double[] expected = {
            25.5,
            1450.,
            -50.,
            25.,
            25.,
            25.
        };
        
        IntStream.range(0, inputs.length)
                .forEach((i) -> {
                    assertThat(result.get(i))
                    .isEqualTo(expected[i], offset(.0001));
                });
    }

    @Test
    public void test_invalid_expression_input() {
        String[] inputs = {
            "5*3+10+",
            "6*6*40--(-4*(5/2))",
            "5*5+5*5+(6//6)*-1",
            "*5*3+10",
            "5*3+1+0+",
            "5*3%+10%"};

        Stream.of(inputs).forEach((e) -> {
            assertThatThrownBy(() -> {
                pe.evaluateExpression(e);
            }).isInstanceOf(EvaluationException.class);
        });
    }

    @Test
    public void test_invalid_relation_input() {
        String[] inputs = {
            "5*3+10<",
            "6=2",
            "5*5!=4*3==2"};

        Stream.of(inputs).forEach((e) -> {
            assertThatThrownBy(() -> {
                pe.evaluateRelation(e);
            }).isInstanceOf(EvaluationException.class);
        });
    }

}
