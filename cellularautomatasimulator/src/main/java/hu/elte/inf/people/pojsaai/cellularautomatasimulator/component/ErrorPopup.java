package hu.elte.inf.people.pojsaai.cellularautomatasimulator.component;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * The popup window that shows errors to the user.
 * @author József Pollák
 */
public class ErrorPopup extends Stage {
        
    public ErrorPopup(Exception e) {
        this(e.getMessage());
    }

    public ErrorPopup(String msg) {
        Label messageLabel = new Label(msg);
        messageLabel.setTextAlignment(TextAlignment.CENTER);
        Button okButton = new Button("Ok");
        okButton.setOnAction((ActionEvent event) -> {
            this.close();
        });

        VBox root = new VBox(messageLabel, okButton);
        root.setAlignment(Pos.CENTER);

        root.getChildren().forEach((node) -> {
            VBox.setMargin(node, new Insets(10));
        });

        Scene scene = new Scene(root);
        this.initStyle(StageStyle.UTILITY);
        this.setAlwaysOnTop(true);
        this.setTitle("Error");
        this.setScene(scene);
    }
    
    /**
     * Sets the parent of this popup window
     * @param parent the parent
     * @return this
     */
    public ErrorPopup setParent(Stage parent){
        this.initOwner(parent);
        return this;
    }
    
    /**
     * Sets a default 5 seconds long countdown timer on this popup. 
     * If the time is out, the window disappeares.
     * @return this
     */
    public ErrorPopup setCountDown(){
        return setCountDown(5000);
    }
    
    /**
     * Sets a countdown timer on this popup. 
     * If the time is out, the window disappeares.
     * @param millis the millis before the popup closes
     * @return this
     */
    public ErrorPopup setCountDown(int millis){
        initFadeOutTransition(millis).play();
        return this;
    }

    private Timeline initFadeOutTransition(int millis) {
        KeyValue kv = new KeyValue(this.opacityProperty(), 0);
        KeyFrame kf = new KeyFrame(Duration.millis(500), kv);
        Timeline t = new Timeline(kf);
        t.setDelay(Duration.millis(millis));
        t.setOnFinished((ActionEvent event) -> {
            this.hide();
        });
        return t;
    }
}
