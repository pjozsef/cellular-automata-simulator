package hu.elte.inf.people.pojsaai.cellularautomatasimulator.component.edit;

import hu.elte.inf.people.pojsaai.cellularautomatasimulator.CAS_GUI;
import hu.elte.inf.people.pojsaai.cellularautomatasimulator.cellularautomaton.CASave;
import hu.elte.inf.people.pojsaai.cellularautomatasimulator.cellularautomaton.CAWrapper;
import hu.elte.inf.people.pojsaai.cellularautomatasimulator.cellularautomaton.RuleValidator;
import hu.elte.inf.people.pojsaai.cellularautomatasimulator.component.ErrorPopup;
import hu.elte.inf.people.pojsaai.cellularautomatasimulator.component.ExportPopup;
import hu.elte.inf.people.pojsaai.cellularautomatasimulator.event.DeactivateEditPaneAnimation;
import hu.elte.inf.people.pojsaai.cellularautomatasimulator.event.EventBus;
import hu.elte.inf.people.pojsaai.cellularautomaton.data.CAInitializer;
import hu.elte.inf.people.pojsaai.cellularautomaton.data.Rule;
import hu.elte.inf.people.pojsaai.cellularautomaton.neighborhood.InvalidInitializatorException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

/**
 * The panel that contains all editor widgets.
 * @author József Pollák
 */
public class EditPane extends AnchorPane {

    private CAWrapper wrapper;
    private final CAS_GUI parent;
    private CAInitializer previousInit;
    private final FileChooser fc;
    private File loadedFile;

    private StateEditorPane stateEditorPane;
    @FXML
    private Button renderButton;
    @FXML
    private Button stopButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button loadButton;
    @FXML
    private Button exportButton;
    @FXML
    private AnchorPane stateEditorContainer;
    @FXML
    private Slider nodeCountSlider;
    @FXML
    private RuleTextArea ruleTextArea;
    @FXML
    private RandomSeedChooser randomSeedChooser;
    @FXML
    private StartStateConfigChooser startStateConfigChooser;

    @SuppressWarnings("LeakingThisInConstructor")
    public EditPane(CAS_GUI parent) throws IOException {
        String path = "/fxml/panels/EditPane.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
        this.parent = parent;
        this.fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Cellular automaton save files", "*.ca"));
        loadedFile = null;
        init();
    }

    private void init() {
        stateEditorPane = new StateEditorPane(this, stateEditorContainer.getPrefWidth(), stateEditorContainer.getPrefHeight());
        EventBus.register(stateEditorPane);
        stateEditorContainer.getChildren().add(stateEditorPane);
        IntegerProperty nodeCountProperty = stateEditorPane.nodeCountProperty();
        DoubleProperty sliderValueProperty = nodeCountSlider.valueProperty();
        Bindings.bindBidirectional(nodeCountProperty, sliderValueProperty);
        stateEditorPane.setNodeCount(2);

        ruleTextArea.setStateCountProperty(stateEditorPane.nodeCountProperty());

        stateEditorPane.selectedEdgeProperty().addListener((ObservableValue<? extends CAEdge> observable, CAEdge oldValue, CAEdge newValue) -> {
            if (oldValue != null) {
                if (!ruleTextArea.getText().endsWith("\n")) {
                    ruleTextArea.setText(ruleTextArea.getText() + "\n");
                }
                ruleTextArea.textProperty().unbindBidirectional(oldValue.getRulesProperty());
            }
            if (newValue == null) {
                ruleTextArea.setText("");
                ruleTextArea.setDisable(true);
            } else {
                ruleTextArea.setDisable(false);
                ruleTextArea.textProperty().bindBidirectional(newValue.getRulesProperty());
                ruleTextArea.requestFocus();
            }
        });

        startStateConfigChooser.setContainer(this);
        startStateConfigChooser.setNodeCountProperty(nodeCountProperty);

        renderButton.setOnAction(this::startRendering);
        stopButton.setOnAction(this::stopRendering);

        renderButton.disableProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            stopButton.disableProperty().set(!renderButton.disableProperty().get());
        });
        stopButton.disableProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            renderButton.disableProperty().set(!stopButton.disableProperty().get());
        });

        saveButton.setOnAction(this::saveConfiguration);
        loadButton.setOnAction(this::loadConfiguration);
        exportButton.setOnAction(this::exportImages);

    }

    private void lookForConfigChanges() throws InvalidInitializatorException {
        RuleValidator.checkForRuleErrors(stateEditorPane);
        CAInitializer init = getInitializer();
        if (wereChangesMade()) {
            previousInit = init;

            parent.getCanvas().setRows(init.getCells().length);
            parent.getCanvas().setColumns(init.getCells()[0].length);
            final int delay = 500;
            if (wrapper != null) {
                EventBus.unregister(wrapper);
            }
            this.wrapper = new CAWrapper(init, parent.getCanvas(), delay);
            EventBus.register(wrapper);
        }
    }

    private boolean wereChangesMade() {
        CAInitializer init;
        try {
            init = getInitializer();
            if (previousInit == null || !init.equals(previousInit)) {
                return true;
            }
        } catch (InvalidInitializatorException ex) {
            return true;
        }
        return false;
    }

    private void startRendering(ActionEvent event) {
        renderButton.setDisable(true);
        parent.getFrameSlider().setDisable(true);
        List<Color> colors = stateEditorPane.getStateColors();
        try {
            lookForConfigChanges();
            wrapper.setColors(colors);
            wrapper.start();
        } catch (InvalidInitializatorException ex) {
            renderButton.setDisable(false);
            new ErrorPopup(ex)
                    .setParent(CAS_GUI.getMainStage())
                    .setCountDown()
                    .show();
        }
    }

    private void stopRendering(ActionEvent event) {
        if (wrapper == null) {
            new ErrorPopup(new NullPointerException("Internal error, wrapper is null, restart the program and contact the developer about how this error occoured!"))
                    .setParent(CAS_GUI.getMainStage())
                    .setCountDown()
                    .show();
        } else {
            stopButton.setDisable(true);
            parent.getFrameSlider().setDisable(false);
            wrapper.interrupt();
        }
    }

    private void saveConfiguration(ActionEvent event) {
        try {
            lookForConfigChanges();

            EventBus.post(new DeactivateEditPaneAnimation(true));
            fc.setTitle("Save cellular automaton...");
            File chosenFile = fc.showSaveDialog(null);
            if (chosenFile != null) {
                fc.setInitialDirectory(new File(chosenFile.getParent()));
                String seed = randomSeedChooser.save();
                Map<String, String> graphicInit = startStateConfigChooser.getSave();
                CAInitializer init = wrapper.getInitializer();
                String extension = chosenFile.getPath().endsWith("ca") ? "" : ".ca";
                new CASave(seed, graphicInit, init)
                        .save(new File(chosenFile.getPath() + extension));
                CAS_GUI.getMainStage().setTitle(chosenFile.getName());
            }
            EventBus.post(new DeactivateEditPaneAnimation(false));
        } catch (InvalidInitializatorException ex) {
            renderButton.setDisable(false);
            new ErrorPopup(ex)
                    .setParent(CAS_GUI.getMainStage())
                    .setCountDown()
                    .show();
        }
    }

    private void loadConfiguration(ActionEvent event) {
        EventBus.post(new DeactivateEditPaneAnimation(true));
        fc.setTitle("Load cellular automaton...");
        File chosenFile = fc.showOpenDialog(null);
        if (chosenFile != null){
            fc.setInitialDirectory(new File(chosenFile.getParent()));
            loadedFile = chosenFile;
            try {
                CASave save = CASave.create(chosenFile);
                randomSeedChooser.load(save.getSeed());
                startStateConfigChooser.load(save.getGraphicInit());
                stateEditorPane.load(save.getCaInit().getRules());
                CAS_GUI.getMainStage().setTitle(chosenFile.getName());
            } catch (IOException ex) {
                Logger.getLogger(EditPane.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        EventBus.post(new DeactivateEditPaneAnimation(false));
    }

    private void exportImages(ActionEvent event) {
        try {
            lookForConfigChanges();
            List<int[][]> cellCache = wrapper.getCellularAutomaton().getCellCache();
            List<Color> colors = stateEditorPane.getStateColors();
            new ExportPopup(cellCache, colors).show();
        } catch (InvalidInitializatorException ex) {
            new ErrorPopup(ex)
                    .setParent(CAS_GUI.getMainStage())
                    .setCountDown()
                    .show();
        }
    }

    private CAInitializer getInitializer() throws InvalidInitializatorException {
        try {
            int[][] cells = startStateConfigChooser.getInitialConfig(stateEditorPane.getNodeCount());
            Map<Integer, List<Rule>> transitionRules = stateEditorPane.getTransitionRules();
            long seed = randomSeedChooser.getSeed();

            return new CAInitializer(cells, transitionRules, seed);
        } catch (IOException ex) {
            throw new InvalidInitializatorException(ex.getMessage(), ex);
        }
    }

    /**
     * Increases the node count of the StateEditorPane.
     */
    @FXML
    public void incNodes() {
        if (stateEditorPane.getNodeCount() < nodeCountSlider.getMax()) {
            stateEditorPane.incNodeCount();
        }
    }

    /**
     * Decreases the node count of the StateEditorPane.
     */
    @FXML
    public void decNodes() {
        if (stateEditorPane.getNodeCount() > 1) {
            stateEditorPane.decNodeCount();
        }
    }

}
