package hu.elte.inf.people.pojsaai.evaluator.postfix;

import hu.elte.inf.people.pojsaai.evaluator.common.token.Token;
import static hu.elte.inf.people.pojsaai.evaluator.common.token.TokenFactory.create;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

/**
 *
 * @author József Pollák
 */
public class PostfixConverterTest{
    private List<Token> input;

    @Test
    public void test_01(){//((50+80)*(-10))
        input = Arrays.asList(create("("),
                create("("),
                create("50"),
                create("+"),
                create("80"),
                create(")"),
                create("*"),
                create("("),
                create("-10"),
                create(")"),
                create(")"));

        List<Token> expResult = Arrays.asList(create("50"),
                create("80"),
                create("+"),
                create("-10"),
                create("*"));
        
        PostfixConverter converter = new PostfixConverter();
        input.stream().forEach((t) -> {
            t.accept(converter);
        });
        converter.close();
        
        assertThat(converter.getPostfixForm()).isEqualTo(expResult);
    }
    
    @Test
    public void test_02(){//(())
        input = Arrays.asList(create("("),
                create("("),
                create(")"),
                create(")"));
        
        List<Token> expResult = new ArrayList<>();
        
        PostfixConverter converter = new PostfixConverter();
        input.stream().forEach((t) -> {
            t.accept(converter);
        });
        converter.close();

        assertThat(converter.getPostfixForm()).isEqualTo(expResult);
    }
    
    public void test_03(){//empty
        input = new ArrayList<>();
        PostfixConverter converter = new PostfixConverter();
        input.stream().forEach((t) -> {
            t.accept(converter);
        });
        converter.close();
        
        List<Token> expResult = new ArrayList<>();
        assertThat(converter.getPostfixForm()).isEqualTo(expResult);
    }

}
