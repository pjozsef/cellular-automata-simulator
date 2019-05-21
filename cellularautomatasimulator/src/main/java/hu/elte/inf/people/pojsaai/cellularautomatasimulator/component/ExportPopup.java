package hu.elte.inf.people.pojsaai.cellularautomatasimulator.component;

import hu.elte.inf.people.pojsaai.cellularautomatasimulator.CAS_GUI;
import hu.elte.inf.people.pojsaai.cellularautomaton.jobs.ImageWriter;
import java.io.File;
import java.util.List;
import java.util.stream.IntStream;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jdk.nashorn.internal.runtime.JSType;

/**
 * The export window.
 * @author József Pollák
 */
public class ExportPopup extends Stage {
    /**
     * The constructor of this window.
     * @param cellCache the list of cellular automaton states
     * @param colors the colors of the states
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public ExportPopup(List<int[][]> cellCache, List<Color> colors) {
        Label messageLabel = new Label("Select the automaton states you want to export:");
        Button okButton = new Button("Ok");
        VBox root = new VBox();
        HBox selection = new HBox();
        selection.setSpacing(10);
        TextField from = new TextField();
        TextField to = new TextField();
        from.setMinWidth(5);
        from.setPromptText("From");
        to.setMinWidth(5);
        to.setPromptText("To");
        selection.getChildren()
                .addAll(
                        from,
                        new Label(" - "),
                        to
                );

        root.getChildren().addAll(messageLabel, selection, okButton);

        root.getChildren().forEach((node) -> {
            VBox.setMargin(node, new Insets(10));
        });

        Scene scene = new Scene(root);
        this.initStyle(StageStyle.UTILITY);
        this.setAlwaysOnTop(true);
        this.setTitle("Export states...");
        this.setScene(scene);
        this.initOwner(CAS_GUI.getMainStage());

        okButton.setOnAction((ActionEvent event) -> {
            if (JSType.isNumber(from.getText()) && JSType.isNumber(to.getText())) {
                DirectoryChooser dc = new DirectoryChooser();
                int fromValue = Integer.parseInt(from.getText());
                if (fromValue < 0) {
                    fromValue = 0;
                }
                int toValue = Integer.parseInt(to.getText());
                if (toValue > cellCache.size()) {
                    toValue = cellCache.size();
                }

                File directory = dc.showDialog(this);
                if (directory != null) {
                    ImageWriter.saveImages(cellCache, fromValue, toValue, convertColors(colors), directory);
                }
                this.close();
            } else {
                new ErrorPopup("Input in from-to range is not a number!")
                        .setParent(this)
                        .setCountDown()
                        .show();
            }
        });
    }

    private java.awt.Color[] convertColors(List<Color> colors) {
        java.awt.Color[] result = new java.awt.Color[colors.size()];
        IntStream.range(0, colors.size()).forEach((i) -> {
            Color base = colors.get(i);
            result[i] = new java.awt.Color((float) base.getRed(), (float) base.getGreen(), (float) base.getBlue());
        });
        return result;
    }
}
