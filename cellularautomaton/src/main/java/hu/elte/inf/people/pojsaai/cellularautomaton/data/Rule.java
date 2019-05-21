package hu.elte.inf.people.pojsaai.cellularautomaton.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Value;
import lombok.experimental.Builder;

/**
 * This immutable value class represents a transition rule of a cellular automaton.
 * @author József Pollák
 */
@Value
@Builder
public class Rule{
    private List<String> expressions;
    private int nextState;
    
    @JsonCreator
    public Rule(
            @JsonProperty("expressions") List<String> expressions, 
            @JsonProperty("nextState") int nextState){
        this.expressions=expressions;
        this.nextState=nextState;
    }
}
