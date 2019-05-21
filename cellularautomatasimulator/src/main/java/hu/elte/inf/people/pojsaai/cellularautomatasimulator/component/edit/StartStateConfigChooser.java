package hu.elte.inf.people.pojsaai.cellularautomatasimulator.component.edit;

import hu.elte.inf.people.pojsaai.cellularautomatasimulator.cellularautomaton.RuleValidator;
import hu.elte.inf.people.pojsaai.cellularautomatasimulator.component.ToggleBar;
import hu.elte.inf.people.pojsaai.cellularautomatasimulator.event.DeactivateEditPaneAnimation;
import hu.elte.inf.people.pojsaai.cellularautomatasimulator.event.EventBus;
import hu.elte.inf.people.pojsaai.cellularautomaton.jobs.ImageReader;
import hu.elte.inf.people.pojsaai.cellularautomaton.neighborhood.InvalidInitializatorException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import lombok.EqualsAndHashCode;
import lombok.Setter;

/**
 * This widget handles the settings of the starting initialization of the cellular automaton.
 * @author József Pollák
 */
@EqualsAndHashCode(callSuper = true)
public class StartStateConfigChooser extends VBox {

    public static final String SELECTED = "Selected";
    public static final String RANDOM = "Random";
    public static final String IMAGE = "Image";
    public static final String COORDINATES = "Coordinates";
    public static final String DELIMITER = "#";

    private File chosenFile;
    private FileChooser fc;
    private IntegerProperty nodeCountProperty;
    private ToggleButton lastSelected;

    @Setter
    private EditPane container;

    @FXML
    private Label infoLabel;
    private final ToggleBar toggleBar;
    private final ImageView configImage;

    private final VBox coordSetupContainer;
    private final TextArea coordInput;

    //settings for random image
    private final VBox randomSetupContainer;
    private RandomSeedChooser seed;
    private Label randomStateLabel, widthLabel, heightLabel, coordWidthLabel, coordHeightLabel;
    private Slider randomStateSlider, heightSlider, widthSlider, coordHeightSlider, coordWidthSlider;

    @SuppressWarnings("LeakingThisInConstructor")
    public StartStateConfigChooser() throws IOException {
        loadFXML();

        toggleBar = ToggleBar
                .builder()
                .add(
                        ToggleBar
                        .buttonBuilder()
                        .text(RANDOM)
                        .actionEvent(this::randomSelected)
                        .build())
                .add(
                        ToggleBar
                        .buttonBuilder()
                        .text(IMAGE)
                        .actionEvent(this::customImageSelected)
                        .build())
                .add(
                        ToggleBar
                        .buttonBuilder()
                        .text(COORDINATES)
                        .actionEvent(this::coordInputSelected)
                        .build())
                .build();

        getChildren().add(toggleBar);
        lastSelected = toggleBar.getSelected();
        toggleBar.setAlignment(Pos.CENTER);

        configImage = new ImageView();
        setupConfigImage();

        randomSetupContainer = new VBox();
        setupRandomSetupContainer();

        coordInput = new TextArea();
        coordSetupContainer = new VBox();
        setupCoordInput();

        getChildren().add(randomSetupContainer);

        coordHeightSlider.valueProperty().bindBidirectional(heightSlider.valueProperty());
        coordWidthSlider.valueProperty().bindBidirectional(widthSlider.valueProperty());
    }

    private void setupConfigImage() {
        fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files", "*.jpg", "*.jpeg", "*.png", "*.gif"));

        configImage.setFitHeight(150);
        configImage.setPreserveRatio(true);
        final String imagePath = "/images/fileOpen.png";
        configImage.setImage(new Image(imagePath));

        configImage.setOnMouseEntered((MouseEvent event) -> {
            configImage.setEffect(new DropShadow(10, Color.BLACK));
        });

        configImage.setOnMouseExited((MouseEvent event) -> {
            configImage.setEffect(null);
        });

        configImage.setOnMouseClicked((MouseEvent event) -> {
            EventBus.post(new DeactivateEditPaneAnimation(true));
            chosenFile = fc.showOpenDialog(null);
            if (chosenFile != null) {
                Image img = new Image("file:" + chosenFile.toString());
                fc.setInitialDirectory(new File(chosenFile.getParent()));
                configImage.setImage(img);
            }
            EventBus.post(new DeactivateEditPaneAnimation(false));
        });
    }

    private void setupRandomSetupContainer() {
        heightSlider = new Slider(5, 100, 10);
        widthLabel = new Label();
        widthLabel.textProperty().bind(Bindings.createStringBinding(
                () -> {
                    return "Width: " + heightSlider.valueProperty().intValue();
                },
                heightSlider.valueProperty()));

        widthSlider = new Slider(5, 100, 10);
        heightLabel = new Label();
        heightLabel.textProperty().bind(Bindings.createStringBinding(
                () -> {
                    return "Height: " + widthSlider.valueProperty().intValue();
                },
                widthSlider.valueProperty()));

        widthSlider.valueProperty().bindBidirectional(heightSlider.valueProperty());

        seed = new RandomSeedChooser();

        randomSetupContainer.getChildren().addAll(
                widthLabel,
                heightSlider,
                heightLabel,
                widthSlider,
                seed
        );

        VBox.setMargin(randomSetupContainer, new Insets(0, 5, 0, 10));
    }

    private void setupCoordInput() {
        coordInput.setPrefHeight(250);
        coordInput.setMinHeight(250);

        coordHeightSlider = new Slider(5, 100, 10);
        coordWidthLabel = new Label();
        coordWidthLabel.textProperty().bind(Bindings.createStringBinding(
                () -> {
                    return "Width: " + coordHeightSlider.valueProperty().intValue();
                },
                coordHeightSlider.valueProperty()));

        coordWidthSlider = new Slider(5, 100, 10);
        coordHeightLabel = new Label();
        coordHeightLabel.textProperty().bind(Bindings.createStringBinding(
                () -> {
                    return "Height: " + coordWidthSlider.valueProperty().intValue();
                },
                coordWidthSlider.valueProperty()));

        coordWidthSlider.valueProperty().bindBidirectional(coordHeightSlider.valueProperty());

        coordInput.textProperty().addListener(this::validateCoordInput);
        coordWidthSlider.valueProperty().addListener(this::validateCoordInput);
        coordHeightSlider.valueProperty().addListener(this::validateCoordInput);

        coordSetupContainer.getChildren().addAll(
                coordWidthLabel,
                coordHeightSlider,
                coordHeightLabel,
                coordWidthSlider,
                coordInput
        );
    }

    private void validateCoordInput(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
        coordInput.setText(coordInput.getText().toUpperCase());
        if (!isCoordsTextValid()) {
            coordInput.setEffect(RuleValidator.getErrorEffect());
        } else {
            coordInput.setEffect(null);
        }
    }

    private boolean isCoordsTextValid() {
        String text = coordInput.getText();
        boolean containsOneRest = text.split("REST").length == 2;

        return Stream
                .of(
                        text.split("\n"))
                .map(e -> e.replaceAll(" ", ""))
                .filter(e -> !e.isEmpty())
                .map(this::coordsValidator)
                .reduce(containsOneRest, (a, b) -> a && b);
    }

    private boolean coordsValidator(String s) {
        if (isCoordinate(s)) {
            String[] numbers = s.split(",|:");
            return Integer.parseInt(numbers[0]) <= widthSlider.valueProperty().intValue()
                    && Integer.parseInt(numbers[1]) <= heightSlider.valueProperty().intValue()
                    && Integer.parseInt(numbers[2]) < nodeCountProperty.get();
        } else if (s.matches("REST:(0|[1-9][0-9]*)")) {
            return Integer.parseInt(s.replaceAll("REST:", "")) < nodeCountProperty.get();
        }
        return false;
    }

    private static boolean isCoordinate(String s) {
        String numberRegex = "[1-9][0-9]*";
        return s.matches(numberRegex + "," + numberRegex + ":" + "(0|" + numberRegex + ")");
    }

    private void randomSelected(ActionEvent event) {
        if (lastSelected != event.getSource()) {
            getChildren().removeAll(configImage, coordSetupContainer);
            getChildren().add(randomSetupContainer);
            lastSelected = (ToggleButton) event.getSource();
        }
    }

    private void customImageSelected(ActionEvent event) {
        if (lastSelected != event.getSource()) {
            getChildren().removeAll(randomSetupContainer, coordSetupContainer);
            getChildren().add(configImage);
            lastSelected = (ToggleButton) event.getSource();
        }
    }

    private void coordInputSelected(ActionEvent event) {
        if (lastSelected != event.getSource()) {
            getChildren().removeAll(randomSetupContainer, configImage);
            getChildren().add(coordSetupContainer);
            lastSelected = (ToggleButton) event.getSource();
        }
    }

    private void loadFXML() throws IOException {
        String path = "/fxml/panels/StartingConfigChooser.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
    }

    /**
     * Returns the initial configuration for the cellular automaton.
     * @param states the number of states
     * @return the starting configuration
     * @throws IOException if the image used as configuration is not found
     * @throws InvalidInitializatorException if the coordinates' rules are invalid
     */
    public int[][] getInitialConfig(int states) throws IOException, InvalidInitializatorException {
        ToggleButton selected = toggleBar.getSelected();

        switch (selected.getText()) {
            case RANDOM:
                return ImageReader.getRandomImage(widthSlider.valueProperty().intValue(), heightSlider.valueProperty().intValue(), states, seed.getSeed());
            case IMAGE:
                if(chosenFile==null){
                    throw new InvalidInitializatorException("Image input is selected, however no image was chosen for starting initialization!");
                }
                int[][] imageCells = ImageReader.readCellsFromImage(chosenFile);
                if (!statesMatch(imageCells, states)) {
                    throw new InvalidInitializatorException("Number of states are less than the states encoded in the image file!");
                }
                return imageCells;
            case COORDINATES:
                if (coordInput.getText().replaceAll("\\s", "").length() == 0) {
                    throw new InvalidInitializatorException("Coordinates textarea is empty!");
                } else if (!isCoordsTextValid()) {
                    throw new InvalidInitializatorException("Text in coordinates textarea is invalid!");
                }
                return createArrayFromCoordinates();
        }
        throw new IllegalStateException("ToggleButton texts were overriden!!");
    }

    private boolean statesMatch(int[][] imageCells, int states) {
        Set<Integer> imageStates = new HashSet<>();
        for (int i = 0; i < imageCells.length; ++i) {
            for (int j = 0; j < imageCells[i].length; ++j) {
                imageStates.add(imageCells[i][j]);
            }
        }

        return imageStates.size() <= states;
    }

    /**
     * Sets the stateCountProperty, representing the number of states.
     * @param stateCountProperty the property for the number of states
     */
    public void setNodeCountProperty(IntegerProperty stateCountProperty) {
        this.nodeCountProperty = stateCountProperty;
        nodeCountProperty.addListener(this::validateCoordInput);
    }

    private int[][] createArrayFromCoordinates() {
        int[][] result = new int[widthSlider.valueProperty().intValue()][heightSlider.valueProperty().intValue()];
        String input = coordInput.getText().replaceAll(" ", "");
        List<String> coords = new ArrayList<>(Arrays.asList(input.split("\n")));
        String rest = "";
        for (String s : coords) {
            if (s.startsWith("REST:")) {
                rest = s;
                break;
            }
        }
        coords.remove(rest);
        rest = rest.replaceAll("REST:", "");
        int fill = Integer.parseInt(rest);
        for (int i = 0; i < result.length; ++i) {
            Arrays.fill(result[i], fill);
        }

        for (String s : coords) {
            if (s.length() > 0) {
                String[] numbers = s.split(",|:");
                int x = Integer.parseInt(numbers[0]) - 1;
                int y = Integer.parseInt(numbers[1]) - 1;
                int state = Integer.parseInt(numbers[2]);
                result[x][y] = state;
            }
        }

        return result;
    }

    /**
     * Returns the user settings collected into a Map of Strings.
     * @return the settings Map
     */
    public Map<String, String> getSave() {
        Map<String, String> result = new HashMap<>();
        result.put(SELECTED, toggleBar.getSelected().getText());
        result.put(RANDOM,
                widthSlider.valueProperty().intValue()
                + DELIMITER
                + heightSlider.valueProperty().intValue()
                + DELIMITER
                + seed.save());
        if (chosenFile != null) {
            result.put(
                    IMAGE,
                    Paths.get(System.getProperty("user.dir"))
                    .relativize(
                            chosenFile.toPath().toAbsolutePath())
                    .toString());
        }
        result.put(COORDINATES,
                widthSlider.valueProperty().intValue()
                + DELIMITER
                + heightSlider.valueProperty().intValue()
                + DELIMITER
                + coordInput.getText());
        return result;
    }

    /**
     * Loads the settings map.
     * @param saved the settings map
     */
    public void load(Map<String, String> saved) {
        String random = saved.get(RANDOM);
        String[] randomSplit = random.split("#");
        widthSlider.setValue(Double.parseDouble(randomSplit[0]));
        heightSlider.setValue(Double.parseDouble(randomSplit[1]));
        seed.load(randomSplit[2]);

        if (saved.get(IMAGE) != null) {
            String imgPath = saved.get(IMAGE);
            if (Files.isRegularFile(Paths.get(imgPath))) {
                chosenFile = new File(saved.get(IMAGE));
                Image img = new Image("file:" + chosenFile.toString());
                configImage.setImage(img);
            }
        }

        String coordinates = saved.get(COORDINATES);
        String[] coordinatesSplit = coordinates.split("#");
        widthSlider.setValue(Double.parseDouble(coordinatesSplit[0]));
        heightSlider.setValue(Double.parseDouble(coordinatesSplit[1]));
        String coordText = coordinatesSplit.length == 3 ? coordinatesSplit[2] : "";
        coordInput.setText(coordText);

        String selected = saved.get(SELECTED);
        ToggleButton button = toggleBar.getButtons().stream().filter(b -> b.getText().equals(selected)).findFirst().get();
        int index = toggleBar.getButtons().indexOf(button);
        toggleBar.setSelected(index);
        button.onActionProperty().get().handle(new ActionEvent(button, null));
    }
}
