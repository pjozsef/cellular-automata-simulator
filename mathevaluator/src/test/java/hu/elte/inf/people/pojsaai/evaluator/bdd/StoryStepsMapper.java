package hu.elte.inf.people.pojsaai.evaluator.bdd;

import hu.elte.inf.people.pojsaai.evaluator.Evaluator;
import hu.elte.inf.people.pojsaai.evaluator.exception.EvaluationException;
import hu.elte.inf.people.pojsaai.evaluator.postfix.PostfixFacade;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import static org.assertj.core.api.Assertions.*;

/**
 *
 * @author József Pollák
 */
public class StoryStepsMapper {

    Evaluator evaluator = new PostfixFacade();
    private String input;
    private double numResult;
    private boolean boolResult;
    private Exception exception;

    @Given("$expression as input")
    public void setInput(String input) {
        this.input = input;
    }

    @When("numerical expression is evaluated")
    public void evalNumExpression() {
        try {
            this.numResult = evaluator.evaluateExpression(input);
        } catch (EvaluationException ee) {
            this.exception = ee;
        }
    }
    
    @When("relational expression is evaluated")
    public void evalRelationalExpression(){
        try {
            this.boolResult = evaluator.evaluateRelation(input);
        } catch (EvaluationException ee) {
            this.exception = ee;
        }
    }

    @Then("result should equal $value")
    public void shouldEqualToDouble(String value) {
        assertThat(numResult).isEqualTo(Double.parseDouble(value));
    }
    
    @Then("result should be $value")
    public void shouldEqualToBoolean(String boolValue){
        boolean value = Boolean.parseBoolean(boolValue);
        assertThat(boolResult).isEqualTo(value);
    }

    @Then("evaluation exception should be thrown")
    public void exceptionMustNotBeNull() {
        assertThat(exception)
                .isNotNull()
                .isInstanceOf(EvaluationException.class);
        exception = null;
    }
}
