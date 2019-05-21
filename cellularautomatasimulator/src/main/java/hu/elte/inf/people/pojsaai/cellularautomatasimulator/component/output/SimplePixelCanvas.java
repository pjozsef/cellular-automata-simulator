package hu.elte.inf.people.pojsaai.cellularautomatasimulator.component.output;

import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import static com.google.common.base.Preconditions.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.canvas.Canvas;

/**
 *
 * @author József Pollák
 */
public class SimplePixelCanvas extends PixelCanvas {

    private final Canvas canvas;
    private final DoubleProperty pixelWidth, pixelHeight;
    private Color[][] colorMatrix;
    protected final IntegerProperty rows, columns;

    public SimplePixelCanvas(double width, double height, int rows, int columns) {
        checkNotNull(rows, "Rows must not be null!");
        checkNotNull(columns, "Columns must not be null!");
        checkArgument(width > 0, "Width must be greater than 0!");
        checkArgument(height > 0, "Height must be greater than 0!");
        setWidth(width);
        setHeight(height);

        this.canvas = new Canvas();
        this.pixelWidth = new SimpleDoubleProperty();
        this.pixelHeight = new SimpleDoubleProperty();

        this.rows = new SimpleIntegerProperty(rows);
        this.columns = new SimpleIntegerProperty(columns);
        this.rows.addListener(this::onSizeChange);
        this.columns.addListener(this::onSizeChange);

        this.colorMatrix = getInitialColors();

        this.pixelWidth.bind(widthProperty().divide(this.columns));
        this.pixelHeight.bind(heightProperty().divide(this.rows));
        this.pixelWidth.addListener(this::onPropertyChange);
        this.pixelHeight.addListener(this::onPropertyChange);

        this.canvas.widthProperty().bind(this.widthProperty());
        this.canvas.heightProperty().bind(this.heightProperty());

        widthProperty().addListener(this::onPropertyChange);
        heightProperty().addListener(this::onPropertyChange);

        getChildren().add(this.canvas);
    }

    private void onPropertyChange(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        repaint();
    }

    private void onSizeChange(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        checkNotNull(newValue, "New value of observable must not be null!");
        checkArgument(newValue.intValue() > 0, "New value must be greater than 0!");
        
        this.colorMatrix = getInitialColors();
        repaint();
    }

    /**
     * {@inheritDoc }
     * @param x
     * @param y
     * @param c 
     */
    @Override
    public void drawPixel(int x, int y, Color c) {
        colorMatrix[x][y] = c;

        //lower estimate for x and y offset
        final double offsetX = Math.floor(x * getPixelWidth().get());
        final double offsetY = Math.floor(y * getPixelHeight().get());
        //upper estimate for width and height
        final double rectangleWidth = Math.ceil(getPixelWidth().get());
        final double rectangleHeight = Math.ceil(getPixelHeight().get());
        GraphicsContext gc = getCanvas().getGraphicsContext2D();
        gc.setFill(c);
        gc.fillRect(offsetX, offsetY, rectangleWidth, rectangleHeight);
    }

    /**
     * {@inheritDoc }
     * @param states
     * @param colors 
     */
    @Override
    public void drawMatrix(int[][] states, List<Color> colors) {
        for (int i = 0; i < states.length; ++i) {
            for (int j = 0; j < states[i].length; ++j) {
                drawPixel(i, j, colors.get(states[i][j]));
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void repaint() {
        drawMatrix(this.colorMatrix);
    }

    private void drawMatrix(Color[][] innerState) {
        for (int i = 0; i < innerState.length; ++i) {
            for (int j = 0; j < innerState[i].length; ++j) {
                drawPixel(i, j, innerState[i][j]);
            }
        }
    }

    private Color[][] getInitialColors() {
        Color[][] initialColors = new Color[rows.get()][columns.get()];
        for (int i = 0; i < initialColors.length; ++i) {
            for (int j = 0; j < initialColors[i].length; ++j) {
                initialColors[i][j] = new Color(0, 0, 0, 0);
            }
        }
        return initialColors;
    }

    /**
     * {@inheritDoc }
     * @return 
     */
    @Override
    public int getRows() {
        return rows.get();
    }
    
    /**
     * {@inheritDoc }
     * @param newRow 
     */
    @Override
    public void setRows(int newRow){
        rows.set(newRow);
    }

    /**
     * {@inheritDoc }
     * @return 
     */
    @Override
    public int getColumns() {
        return columns.get();
    }
    
    /**
     * {@inheritDoc }
     * @param newColumn 
     */
    @Override
    public void setColumns(int newColumn){
        columns.set(newColumn);
    }

    /**
     * {@inheritDoc }
     * @return 
     */
    @Override
    protected Canvas getCanvas() {
        return this.canvas;
    }

    /**
     * {@inheritDoc }
     * @return 
     */
    @Override
    protected DoubleProperty getPixelWidth() {
        return this.pixelWidth;
    }

    /**
     * {@inheritDoc }
     * @return 
     */
    @Override
    protected DoubleProperty getPixelHeight() {
        return this.pixelHeight;
    }
}
