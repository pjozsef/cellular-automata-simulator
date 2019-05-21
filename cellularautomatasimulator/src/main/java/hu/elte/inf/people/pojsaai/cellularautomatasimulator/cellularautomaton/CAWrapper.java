package hu.elte.inf.people.pojsaai.cellularautomatasimulator.cellularautomaton;

import com.google.common.eventbus.Subscribe;
import hu.elte.inf.people.pojsaai.cellularautomatasimulator.CAS_GUI;
import hu.elte.inf.people.pojsaai.cellularautomatasimulator.component.ErrorPopup;
import hu.elte.inf.people.pojsaai.cellularautomatasimulator.component.output.PixelCanvas;
import hu.elte.inf.people.pojsaai.cellularautomatasimulator.event.DrawMatrix;
import hu.elte.inf.people.pojsaai.cellularautomatasimulator.event.EventBus;
import hu.elte.inf.people.pojsaai.cellularautomatasimulator.event.IncreaseSliderRange;
import hu.elte.inf.people.pojsaai.cellularautomaton.CellularAutomaton2D;
import hu.elte.inf.people.pojsaai.cellularautomaton.data.CAInitializer;
import hu.elte.inf.people.pojsaai.evaluator.exception.EvaluationException;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import lombok.Setter;

/**
 * A wrapper class around a CellularAutomaton2D object. This class connects the GUI and CA layers.
 * @author József Pollák
 */
public class CAWrapper {

    private final CellularAutomaton2D automaton;
    private final PixelCanvas canvas;
    @Setter
    private List<Color> colors;
    private final long preferredSpeed;
    private Thread renderThread;
    private final ObservableList<int[][]> automataStates;
    private boolean interrupted;

    public CAWrapper(CAInitializer init, PixelCanvas canvas, long preferredSpeed) {
        this.interrupted = false;
        this.automaton = new CellularAutomaton2D(init);
        this.canvas = canvas;
        this.preferredSpeed = preferredSpeed;
        this.automataStates = FXCollections.observableArrayList();
    }

    public CAWrapper(CAInitializer init, List<Color> colors, PixelCanvas canvas, long preferredSpeed) {
        this(init, canvas, preferredSpeed);
        this.colors = colors;
    }

    /**
     * Subscription method for the EventBus that listens matrix draw events.
     * @param e the event object
     */
    @Subscribe
    public void handleDrawEvent(DrawMatrix e) {
        if (renderThread == null) {
            canvas.drawMatrix(automataStates.get(e.getIndex()), colors);
        }
    }

    /**
     * Starts the rendering of new states in the automaton. After each new state, a matrix redraw event is fired.
     */
    @SuppressWarnings("SleepWhileInLoop")
    public void start() {
        interrupted = false;
        this.renderThread = new Thread(() -> {
            try {
                updateGUI();//print initial starting state

                while (true) {
                    if (interrupted) {
                        renderThread = null;
                        return;
                    }
                    long start = System.currentTimeMillis();
                    automaton.nextState();
                    sleepIfNeeded(start);
                    updateGUI();
                }
            } catch (EvaluationException ex) {
                Platform.runLater(() -> {
                    new ErrorPopup(ex)
                            .setParent(CAS_GUI.getMainStage())
                            .setCountDown()
                            .show();
                });
            } catch (InterruptedException ex) {
            }
        });
        renderThread.setDaemon(true);
        renderThread.start();

    }

    private void updateGUI() {
        final int index = automaton.getCurrentStateIndex();
        Platform.runLater(() -> {
            automataStates.add(automaton.getState(index));
            EventBus.post(new IncreaseSliderRange(index));
            canvas.drawMatrix(automataStates.get(index), colors);
        });
    }

    private void sleepIfNeeded(long start) throws InterruptedException {
        long diff = System.currentTimeMillis() - start;
        if (diff < preferredSpeed) {

            Thread.sleep(preferredSpeed - diff);
        }
    }

    /**
     * Stops the rendering of new states.
     */
    public void interrupt() {
        interrupted = true;
    }

    /**
     * Returns the initializer object of the automaton.
     * @return the initializer
     */
    public CAInitializer getInitializer() {
        return automaton.getInitializer();
    }

    /**
     * Returns the automaton object.
     * @return the automaton
     */
    public CellularAutomaton2D getCellularAutomaton() {
        return automaton;
    }
}
