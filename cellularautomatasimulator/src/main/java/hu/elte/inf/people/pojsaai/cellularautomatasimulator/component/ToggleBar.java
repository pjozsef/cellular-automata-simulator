package hu.elte.inf.people.pojsaai.cellularautomatasimulator.component;

import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

/**
 * A togglebar class that can have arbitrary number of ToggleButtons
 * @author József Pollák
 */
@Getter
public class ToggleBar extends HBox {

    private final ToggleGroup group;    
    private final List<ToggleButton> buttons;

    @Builder
    public ToggleBar(@Singular("add") List<ToggleButton> buttons) {
        group = new ToggleGroup();
        group.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) -> {
            if(newValue==null){
                group.selectToggle(oldValue);
            }
        });
        this.buttons = buttons;
        this.buttons.forEach(b -> {
            b.setToggleGroup(group);
            getChildren().add(b);
        });
        
        group.selectToggle(buttons.get(0));     
    }

    @Builder(
            builderMethodName = "buttonBuilder",
            buildMethodName = "build",
            builderClassName = "ButtonBuilder")
    private static ToggleButton innerButtonBuilder(String text, EventHandler<ActionEvent> actionEvent) {
        ToggleButton tb = new ToggleButton(text);
        tb.setOnAction(actionEvent);
        return tb;
    }
    
    /**
     * Sets the selected button by index
     * @param index the index of the selected button
     */
    public void setSelected(int index){
        group.selectToggle(buttons.get(index));
    }
    
    /**
     * Returns the count of buttons.
     * @return the count of buttons
     */
    public int getButtonCount(){
        return buttons.size();
    }
    
    /**
     * Returns the selected ToggleButton.
     * @return the selected ToggleButton
     */
    public ToggleButton getSelected(){
        return (ToggleButton)group.getSelectedToggle();
    }

}
