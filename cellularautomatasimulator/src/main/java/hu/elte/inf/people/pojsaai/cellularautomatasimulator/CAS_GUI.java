package hu.elte.inf.people.pojsaai.cellularautomatasimulator;

import com.google.common.eventbus.Subscribe;
import hu.elte.inf.people.pojsaai.cellularautomatasimulator.component.edit.EditPane;
import hu.elte.inf.people.pojsaai.cellularautomatasimulator.component.output.PixelCanvas;
import hu.elte.inf.people.pojsaai.cellularautomatasimulator.component.output.PixelCanvasFactory;
import hu.elte.inf.people.pojsaai.cellularautomatasimulator.event.DeactivateEditPaneAnimation;
import hu.elte.inf.people.pojsaai.cellularautomatasimulator.event.DrawMatrix;
import hu.elte.inf.people.pojsaai.cellularautomatasimulator.event.EventBus;
import hu.elte.inf.people.pojsaai.cellularautomatasimulator.event.IncreaseSliderRange;
import java.io.IOException;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Getter;

/**
 * The application class that starts the graphical user interface.
 * @author József Pollák
 */
@Getter
public class CAS_GUI extends Application {

    private boolean isAnimated = false;
    private boolean isPaneOut = false;
    private boolean locked = false;
    private double x, y;
    private final int EDITPANE_ANIMATION_SPEED = 400;
    private static final int SLIDER_OFFSET = 50;
    private static final int EDITPANE_WIDTH = 420;

    private static Stage mainStage;
    private EditPane editPane;
    private Slider frameSlider;
    private PixelCanvas canvas;
    private Pane root;
    private ScrollPane editPaneContainer;
    private Rectangle shadow;
    private EventHandler<MouseEvent> mouseInHandler;
    private EventHandler<MouseEvent> mouseOutHandler;

    /**
     * {@inheritDoc }
     * @param stage
     * @throws Exception 
     */
    @Override
    public void start(Stage stage) throws Exception {
        EventBus.register(this);

        mainStage = stage;
        mainStage.setTitle("Unsaved automaton...");

        root = new AnchorPane();
        Scene scene = new Scene(root);
        root.setMinHeight(600);
        root.setMinWidth(1000);

        setupRootListeners();

        setupEditPaneContainer();
        setupEditPaneContainerShadow();

        setupFrameSlider();
        setupPixelCanvas();

        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

    }

    private void setupEditPaneContainer() throws IOException {
        editPaneContainer = new ScrollPane();
        editPaneContainer.setMinWidth(EDITPANE_WIDTH);
        editPane = new EditPane(this);
        editPaneContainer.setContent(editPane);
        root.getChildren().add(editPaneContainer);

        setupEditPaneListeners(root, editPaneContainer);
    }

    private void setupPixelCanvas() {
        canvas = PixelCanvasFactory.simpleCanvas()
                .width(300)
                .height(300)
                .rows(5)
                .columns(5)
                .build();
        canvas.prefHeightProperty().bind(root.heightProperty().subtract(SLIDER_OFFSET + 10));
        canvas.prefWidthProperty().bind(root.heightProperty().subtract(SLIDER_OFFSET + 10));
        canvas.translateXProperty().bind(root.widthProperty().subtract(canvas.widthProperty()).divide(2));

        root.getChildren().add(canvas);
        canvas.toBack();
    }

    private void setupFrameSlider() {
        frameSlider = new Slider(0, 0, 0);
        frameSlider.prefWidthProperty().bind(root.widthProperty().multiply(.8));
        frameSlider.translateXProperty().bind(
                root.widthProperty().
                subtract(frameSlider.widthProperty())
                .divide(2)
        );
        frameSlider.translateYProperty().bind(
                root.heightProperty()
                .subtract(SLIDER_OFFSET / 4. * 3)
        );

        frameSlider.setShowTickLabels(true);
        frameSlider.setShowTickMarks(true);
        frameSlider.setMajorTickUnit(10);
        frameSlider.setMinorTickCount(9);
        frameSlider.setSnapToTicks(true);
        frameSlider.setBlockIncrement(1);

        root.getChildren().add(frameSlider);
        frameSlider.toBack();

        frameSlider.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            EventBus.post(new DrawMatrix(newValue.intValue()));
        });
    }

    /**
     * Subscription method for the EventBus that listens for slider range increase events.
     * @param e the event object
     */
    @Subscribe
    public void increaseFrameSliderRange(IncreaseSliderRange e) {
        int newValue = e.getNewMax();
        frameSlider.setMax(newValue);
        frameSlider.setValue(newValue);
    }

    private void setupEditPaneListeners(Region root, Region container) {
        final DoubleBinding mouseOut = root.widthProperty().subtract(50);
        final DoubleBinding mouseIn = root.widthProperty().subtract(container.widthProperty());
        container.translateXProperty().bind(mouseOut);

        Interpolator interpolator = Interpolator.SPLINE(0, .5, .6, 1);

        mouseInHandler = mouseInEvent(container, root, mouseIn, interpolator);
        container.addEventHandler(MouseEvent.MOUSE_ENTERED, mouseInHandler);
        mouseOutHandler = mouseOutEvent(container, root, mouseOut, interpolator);
        container.addEventHandler(MouseEvent.MOUSE_EXITED, mouseOutHandler);

        container.prefHeightProperty().bind(root.heightProperty());

        root.widthProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) -> {
            container.translateXProperty().bind(mouseOut);
        });
    }

    private EventHandler<MouseEvent> mouseInEvent(Region node, Region root, DoubleBinding mouseIn, Interpolator interpolator) {
        return (MouseEvent event) -> {
            isAnimated = true;
            node.translateXProperty().unbind();
            KeyValue kv = new KeyValue(node.translateXProperty(), root.widthProperty().get() - node.widthProperty().get(), interpolator);
            KeyFrame kf = new KeyFrame(Duration.millis(EDITPANE_ANIMATION_SPEED), kv);
            Timeline t = new Timeline(kf);

            t.setOnFinished((ActionEvent event1) -> {
                isPaneOut = true;
                if (!isAnimated) {
                    node.translateXProperty().bind(mouseIn);
                    isAnimated = false;
                }
            });
            t.play();
        };
    }

    private EventHandler<MouseEvent> mouseOutEvent(Region node, Region root, DoubleBinding mouseOut, Interpolator interpolator) {
        return (MouseEvent event) -> {
            isAnimated = true;
            node.translateXProperty().unbind();
            KeyValue kv = new KeyValue(node.translateXProperty(), root.widthProperty().get() - 50, interpolator);
            KeyFrame kf = new KeyFrame(Duration.millis(EDITPANE_ANIMATION_SPEED), kv);

            Timeline t = new Timeline(kf);
            t.setOnFinished((ActionEvent event1) -> {
                isPaneOut = false;
                if (!isAnimated) {
                    node.translateXProperty().bind(mouseOut);
                    isAnimated = false;
                }
            });
            t.play();
        };
    }

    private void enableMouseListeners() {
        editPaneContainer.addEventHandler(MouseEvent.MOUSE_ENTERED, mouseInHandler);
        editPaneContainer.addEventHandler(MouseEvent.MOUSE_EXITED, mouseOutHandler);
    }

    private void disableMouseListeners() {
        editPaneContainer.removeEventHandler(MouseEvent.MOUSE_ENTERED, mouseInHandler);
        editPaneContainer.removeEventHandler(MouseEvent.MOUSE_EXITED, mouseOutHandler);
    }

    /**
     * Subscription method for the EventBus that listens for activation and deactivation of the editor panel.
     * @param e the event object
     */
    @Subscribe
    public void updateMouseListeners(DeactivateEditPaneAnimation e) {
        if (!locked) {
            if (e.isDeactivated()) {
                disableMouseListeners();
            } else {
                enableMouseListeners();
            }
        }
    }

    /**
     * Returns the main stage of the application.
     * @return the main stage
     */
    public static Stage getMainStage() {
        return mainStage;
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void setupRootListeners() {
        root.addEventHandler(KeyEvent.KEY_PRESSED, this::keyhandler);

        root.addEventHandler(MouseEvent.MOUSE_MOVED, (MouseEvent e) -> {
            x = e.getX();
            y = e.getY();
        });
    }

    private void keyhandler(KeyEvent t) {
        if (t.getCode().equals(KeyCode.SPACE) && t.isControlDown()) {
            if (locked) {
                if (!editPane.contains(x - editPaneContainer.getTranslateX(), y - editPaneContainer.getTranslateY())) {
                    mouseOutHandler.handle(null);
                }
                enableMouseListeners();
            } else {
                disableMouseListeners();
                if (!isPaneOut) {
                    mouseInHandler.handle(null);
                }
            }
            locked = !locked;
        }
    }

    private void setupEditPaneContainerShadow() {
        shadow = new Rectangle(EDITPANE_WIDTH, Screen.getPrimary().getVisualBounds().getHeight(), new Color(0, 0, 0, .5));
        shadow.translateXProperty().bind(editPaneContainer.translateXProperty().subtract(10));
        shadow.translateYProperty().bind(editPaneContainer.translateYProperty());
        shadow.setEffect(new BoxBlur(20, 20, 3));
        root.getChildren().add(shadow);
        shadow.toBack();
    }

}
