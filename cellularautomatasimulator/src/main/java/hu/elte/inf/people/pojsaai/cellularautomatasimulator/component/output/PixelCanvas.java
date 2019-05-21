package hu.elte.inf.people.pojsaai.cellularautomatasimulator.component.output;

import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

/**
 * Abstract class, defining the interface of a PixelCanvas object.
 * @author József Pollák
 */
public abstract class PixelCanvas extends AnchorPane {
    /**
     * Draws a pixel in the canvas.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param c the color
     */
    public abstract void drawPixel(int x, int y, Color c);

    /**
     * Draws a whole matrix.
     * @param states the state matrix
     * @param colors the colors
     */
    public abstract void drawMatrix(int[][] states, List<Color> colors);

    /**
     * Repaints the canvas.
     */
    public abstract void repaint();

    /**
     * Returns the row count of the canvas
     * @return the row count of the canvas
     */
    public abstract int getRows();

    /**
     * Sets the row count of the canvas.
     * @param newRow the row count of the canvas
     */
    public abstract void setRows(int newRow);
    
    /**
     * Returns the column count of the canvas
     * @return the column count of the canvas
     */
    public abstract int getColumns();
    
    /**
     * Sets the column count of the canvas
     * @param newColumn the column count of the canvas
     */
    public abstract void setColumns(int newColumn);

    /**
     * Returns the wrapped Canvas object
     * @return the wrapped Canvas
     */
    protected abstract Canvas getCanvas();

    /**
     * Returns the width of a pixel depending on the size of the widget and the number of columns.
     * @return the width of a pixel
     */
    protected abstract DoubleProperty getPixelWidth();

    /**
     * Returns the height of a pixel depending on the size of the widget and the number of rows.
     * @return the height of a pixel
     */
    protected abstract DoubleProperty getPixelHeight();

}
