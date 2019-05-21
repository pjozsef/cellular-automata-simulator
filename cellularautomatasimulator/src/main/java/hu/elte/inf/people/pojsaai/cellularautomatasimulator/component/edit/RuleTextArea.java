package hu.elte.inf.people.pojsaai.cellularautomatasimulator.component.edit;

import hu.elte.inf.people.pojsaai.cellularautomatasimulator.cellularautomaton.RuleValidator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextArea;
import lombok.Getter;

/**
 * A TextArea subclass that validates its content upon typing.
 * @author József Pollák
 */
public class RuleTextArea extends TextArea {
    private static int errors;
    
    @Getter
    private final BooleanProperty validProperty;
    private IntegerProperty stateCountProperty;


    public RuleTextArea() {
        this.validProperty = new SimpleBooleanProperty(true);
        this.textProperty().addListener(this::convertLowercaseToUppercase);
        this.textProperty().addListener(this::checkInputCorrectness);
    }

    private void convertLowercaseToUppercase(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        this.setText(newValue.toUpperCase());
    }
    
    private void checkInputCorrectness(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
        if (RuleValidator.isInputCorrect(textProperty().get(), stateCountProperty.get())) {
            --errors;
            this.setEffect(null);
        } else {
            ++errors;
            this.setEffect(RuleValidator.getErrorEffect());
        }
    }
       
    /**
     * Sets the stateCountProperty, representing the number of states.
     * @param stateCountProperty the property for the number of states
     */
    public void setStateCountProperty(IntegerProperty stateCountProperty) {
        this.stateCountProperty = stateCountProperty;
        this.stateCountProperty.addListener(this::checkInputCorrectness);
    }
}
