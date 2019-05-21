package hu.elte.inf.people.pojsaai.cellularautomatasimulator.component.edit;

import hu.elte.inf.people.pojsaai.cellularautomatasimulator.cellularautomaton.RuleValidator;
import hu.elte.inf.people.pojsaai.cellularautomatasimulator.event.EventBus;
import hu.elte.inf.people.pojsaai.cellularautomatasimulator.misc.AnimationSpeed;
import java.util.Objects;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import lombok.Getter;

/**
 * This class represents a transition between two automaton states.
 * @author József Pollák
 */
@Getter
public class CAEdge extends Group {

    private static final String imagePath = "/images/arrow.png";
    private final static ImagePattern directionIndicatorFill = new ImagePattern(new Image(imagePath));

    private final Line line;
    private final Circle directionIndicator;
    private final CANode start, end;
    private final StringProperty rulesProperty;
    private final IntegerProperty stateCountProperty;

    public CAEdge(Line inputLine, CANode start, CANode end, IntegerProperty stateCountProperty, String rules) {
        this.start = start;
        this.end = end;
        this.line = inputLine;
        this.stateCountProperty = stateCountProperty;
        line.startXProperty().bind(start.getCenterXProperty());
        line.startYProperty().bind(start.getCenterYProperty());
        line.endXProperty().bind(end.getCenterXProperty());
        line.endYProperty().bind(end.getCenterYProperty());

        directionIndicator = new Circle(0, directionIndicatorFill);
        initDirectionIndicator();

        this.rulesProperty = new SimpleStringProperty(rules);
        this.rulesProperty.addListener(this::checkInputCorrectness);
        this.stateCountProperty.addListener(this::checkInputCorrectness);

        this.getChildren().addAll(this.line, directionIndicator);

    }
    
    public CAEdge(Line inputLine, CANode start, CANode end, IntegerProperty stateCountProperty){
        this(inputLine, start, end, stateCountProperty, "");
    }

    private void initDirectionIndicator() {
        KeyFrame kf;
        KeyValue kv1 = new KeyValue(directionIndicator.radiusProperty(), 10);
        kf = new KeyFrame(Duration.millis(50), kv1);

        new Timeline(kf).play();

        directionIndicator.centerXProperty().bind(
                linearInterpolation(line.startXProperty(), line.endXProperty(), 0.2)
        );
        directionIndicator.centerYProperty().bind(
                linearInterpolation(line.startYProperty(), line.endYProperty(), 0.2)
        );
        directionIndicator.rotateProperty().bind(
                Bindings.createDoubleBinding(() -> {
                    double x = line.startXProperty().get() - line.endXProperty().get();
                    double y = line.startYProperty().get() - line.endYProperty().get();
                    return Math.toDegrees(Math.atan2(y, x)) - 90;
                },
                line.startXProperty(),
                line.startYProperty(),
                line.endXProperty(),
                line.endYProperty()));
        
        directionIndicator.setOnMouseClicked(this::handleClick);
    }
    
    private void handleClick(MouseEvent event){
        if(event.isShiftDown() && event.getButton().equals(MouseButton.PRIMARY)){
            EventBus.post(this);
        }
    }

    private void checkInputCorrectness(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
        if (RuleValidator.isInputCorrect(rulesProperty.get(), stateCountProperty.get())) {
            directionIndicator.setEffect(null);
        } else {
            directionIndicator.setEffect(RuleValidator.getErrorEffect());
        }
    }

    public void unbind() {
        line.startXProperty().unbind();
        line.startYProperty().unbind();
        line.endXProperty().unbind();
        line.endYProperty().unbind();
    }

    private NumberBinding linearInterpolation(DoubleExpression start, DoubleExpression end, double ratio) {
        return Bindings.add(
                (start.multiply(ratio)),
                (end.multiply(1 - ratio)));
    }

    /**
     * Returns the animation that is played upon edge delete.
     * @return the animation that is played upon edge delete
     */
    public Timeline getRemovalAnimation() {
        KeyFrame kf;
        KeyValue kv1 = new KeyValue(line.startXProperty(), line.endXProperty().get());
        KeyValue kv2 = new KeyValue(line.startYProperty(), line.endYProperty().get());
        kf = new KeyFrame(Duration.millis(AnimationSpeed.ANIMATION_FAST.speed()), kv1, kv2);
        return new Timeline(kf);
    }
    
    /**
     * Returns if parameter state is either the ending or starting state of this transition.
     * @param node the state to test
     * @return true if state is either the ending or starting state of this transition, otherwise false
     */
    public boolean contains(CANode node){
        return start.equals(node) || end.equals(node);
    }

    /**
     * Returns if the parameter Circle is the directionIndicator belonging to this object.
     * @param c the Circle
     * @return if the parameter Circle is the directionIndicator belonging to this object
     */
    public boolean hasDirectionIndicator(Circle c) {
        return c.getCenterX() == directionIndicator.getCenterX() && c.getCenterY() == directionIndicator.getCenterY();
    }

    /**
     * Grows the directionIndicator circle of this transition, indicating that this transition is selected.
     */
    public void grow() {
        KeyValue kv1 = new KeyValue(directionIndicator.radiusProperty(), 15);
        KeyFrame kf = new KeyFrame(Duration.millis(AnimationSpeed.ANIMATION_FAST.speed()), kv1);
        new Timeline(kf).play();
    }

    /**
     * Shrinks the directionIndicator circle of this transition, indicating that this transition is deselected.
     */
    public void shrink() {
        KeyValue kv1 = new KeyValue(directionIndicator.radiusProperty(), 10);
        KeyFrame kf = new KeyFrame(Duration.millis(AnimationSpeed.ANIMATION_FAST.speed()), kv1);
        new Timeline(kf).play();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.line);
        hash = 89 * hash + Objects.hashCode(this.directionIndicator);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CAEdge other = (CAEdge) obj;

        return this.start.getStateNumber() == other.start.getStateNumber()
                && this.end.getStateNumber() == other.end.getStateNumber();
    }
}
