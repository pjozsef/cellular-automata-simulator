package hu.elte.inf.people.pojsaai.cellularautomatasimulator.component.edit;

import hu.elte.inf.people.pojsaai.cellularautomatasimulator.misc.Range;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * This widget is used to supply a seed value for random generation.
 * @author József Pollák
 */
@Value
@EqualsAndHashCode(callSuper = false)
public class RandomSeedChooser extends HBox {
    private static final String seedRegex = "[A-Z]{3}";

    private Label label;
    private TextField seedField;
    private Button randomButton;
    private Range uppercaseRange;
    private Range lowercaseRange;

    /**
     * Creates a new RandomSeedChooser object.
     */
    public RandomSeedChooser() {
        label = new Label("Seed value  ");
        seedField = new TextField("AAA");
        randomButton = new Button("Random seed");
        uppercaseRange = new Range(65, 90);
        lowercaseRange = new Range(97, 122);

        seedField.textProperty().addListener(this::validateText);
        randomButton.setOnAction(this::buttonAction);

        getChildren().addAll(label, seedField, randomButton);
    }

    private void validateText(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        String updatedValue = "";

        char[] chars = newValue.toCharArray();
        for (char c : chars) {
            if (lowercaseRange.withinRange(c)) {
                char newChar = (char)(c-32);
                updatedValue += newChar;
            } else if (uppercaseRange.withinRange(c)) {
                updatedValue += c;
            }
        }

        if (updatedValue.length() > 3) {
            updatedValue = updatedValue.substring(0, 3);
        }

        if (!newValue.equals(updatedValue)) {
            seedField.setText(updatedValue);
        }
    }

    private void buttonAction(ActionEvent event) {
        String newString = "";
        for (int i = 0; i < 3; ++i) {
            newString += (char) uppercaseRange.randomWithinRange();
        }
        seedField.setText(newString);
    }
    
    /**
     * Returns the seed value.
     * @return the seed value
     */
    public long getSeed(){
        long seed = 0;
        char[] chars = seedField.getText().toCharArray();
        for(int i=0; i<chars.length; ++i){
            seed += chars[i]*Math.pow(10, i);
        }
        return seed;
    }
    
    /**
     * Returns the savefile representation of the seed value.
     * @return the savefile representation of the seed value
     */
    public String save(){
        return seedField.getText();
    }
    
    /**
     * Loads the widget with the given String, if it is a valid String. Otherwise an exception is thrown.
     * @param seed the new seed String
     */
    public void load(String seed){
        validateLoadString(seed);
        seedField.setText(seed);
    }
    
    private void validateLoadString(String seed){
        if(!seed.matches(seedRegex)){
            throw new IllegalArgumentException("SeedString "+seed+" does not match seed format: "+seedRegex);
        }
    }
}
