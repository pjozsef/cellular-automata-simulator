package hu.elte.inf.people.pojsaai.cellularautomatasimulator.component.edit;

import hu.elte.inf.people.pojsaai.cellularautomatasimulator.event.DeactivateEditPaneAnimation;
import hu.elte.inf.people.pojsaai.cellularautomatasimulator.event.EventBus;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.Getter;

/**
 * This class represents a state of an automaton.
 * @author József Pollák
 */
@Getter
public class CANode extends Group {

    private final static Color[] colors = {
        Color.web("#58110d"),
        Color.web("#915225"),
        Color.web("#ac9e34"),
        Color.web("#74be40"),
        Color.web("#238c3f"),
        Color.web("#156f6b"),
        Color.web("#26156d"),
        Color.web("#531a7a"),
        Color.web("#962894"),
        Color.web("#992941")
    };

    boolean isStartingPoint;
    private ColorPicker cp;
    private final Circle innerCircle;
    private final Circle outline;
    private final DoubleProperty centerXProperty;
    private final DoubleProperty centerYProperty;
    private final Label stateLabel;
    private final int stateNumber;

    public CANode(int stateNumber, double centerX, double centerY, double radius) {
        innerCircle = new Circle(centerX, centerY, radius);
        this.centerXProperty = innerCircle.centerXProperty();
        this.centerYProperty = innerCircle.centerYProperty();

        this.stateNumber = stateNumber;

        this.stateLabel = new Label(stateNumber + "");
        stateLabel.translateXProperty().bind(getCenterXProperty().subtract(stateLabel.widthProperty().divide(2.)));
        stateLabel.translateYProperty().bind(getCenterYProperty().subtract(stateLabel.heightProperty().divide(2.)));
        stateLabel.setMouseTransparent(true);
        stateLabel.textFillProperty().bind(Bindings.createObjectBinding(() -> {
            if (((Color) innerCircle.fillProperty().get()).getBrightness() < .6) {
                return Color.WHITESMOKE;
            } else {
                return Color.BLACK;
            }
        }, innerCircle.fillProperty()));

        initColorPicker();

        outline = new Circle(centerX, centerY, radius * 1.1, Color.BLACK);
        outline.centerXProperty().bind(this.centerXProperty);
        outline.centerYProperty().bind(this.centerYProperty);
        getChildren().addAll(cp, outline, innerCircle, stateLabel);
    }

    private void initColorPicker() {
        cp = new ColorPicker(colors[stateNumber%colors.length]);
        cp.setVisible(false);
        cp.showingProperty().addListener((ObservableValue<? extends Object> observable, Object oldValue, Object newValue) -> {
            EventBus.post(new DeactivateEditPaneAnimation(cp.isShowing()));
        });
        this.setOnMouseClicked((MouseEvent e) -> {
            if (e.getButton().equals(MouseButton.SECONDARY)) {
                cp.setTranslateX(this.centerXProperty.doubleValue());
                cp.setTranslateY(this.centerYProperty.doubleValue());
                cp.show();
            }
        });
        innerCircle.fillProperty().bind(cp.valueProperty());
        cp.valueProperty().addListener((ObservableValue<? extends Color> observable, Color oldValue, Color newValue) -> {
            if (newValue!=null) {
                colors[stateNumber % colors.length] = newValue;
            }
        });
    }

    public double getCenterX() {
        return this.centerXProperty.doubleValue();
    }

    public double getCenterY() {
        return this.centerYProperty.doubleValue();
    }

    public double getRadius() {
        return this.innerCircle.getRadius();
    }

    public boolean hasCircle(Circle c) {
        return this.innerCircle.getCenterX() == c.getCenterX()
                && this.innerCircle.getCenterY() == c.getCenterY()
                && this.innerCircle.getRadius() == c.getRadius();
    }

}
