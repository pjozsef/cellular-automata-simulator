package hu.elte.inf.people.pojsaai.evaluator.postfix;

import hu.elte.inf.people.pojsaai.evaluator.exception.EvaluationException;
import hu.elte.inf.people.pojsaai.evaluator.common.token.Token;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import static hu.elte.inf.people.pojsaai.evaluator.common.token.TokenFactory.create;
import static hu.elte.inf.people.pojsaai.evaluator.common.Lexer.tokenize;

/**
 *
 * @author József Pollák
 */
public class LexerTest {

    private String input;
    private List<Token> expectedList;

    @Before
    public void setUp() {
        expectedList = new ArrayList<>();
    }

    @Test
    public void test_01() {
        input = "+-*///";

        createList(expectedList,
                create("+"),
                create("-"),
                create("*"),
                create("/"),
                create("/"),
                create("/")
        );
        assertThat(tokenize(input)).isEqualTo(expectedList);
    }

    @Test
    public void test_02()   {
        input = "6000%3+9/3";
        createList(expectedList,
                create("6000"),
                create("%"),
                create("3"),
                create("+"),
                create("9"),
                create("/"),
                create("3")
        );
        assertThat(tokenize(input)).isEqualTo(expectedList);
    }

    @Test
    public void test_03()  {
        input = "-600-9*(-6%9)";
        createList(expectedList,
                create("-600"),
                create("+"),
                create("-9"),
                create("*"),
                create("("),
                create("-6"),
                create("%"),
                create("9"),
                create(")")
        );
        assertThat(tokenize(input)).isEqualTo(expectedList);
    }

    @Test
    public void test_04()   {
        input = "600$+900£";
        assertThatThrownBy(()->{
            tokenize(input);
        }).isInstanceOf(EvaluationException.class);
    }

    private void createList(List<Token> list, Token... tokens) {
        Stream.of(tokens).forEach(list::add);
    }

    private void printList(List l) {
        l.stream().forEach(System.out::println);
    }

}
