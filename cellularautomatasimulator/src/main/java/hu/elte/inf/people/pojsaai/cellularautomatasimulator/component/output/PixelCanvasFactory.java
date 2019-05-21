package hu.elte.inf.people.pojsaai.cellularautomatasimulator.component.output;

import lombok.Builder;

/**
 * The factory class for creating a PixelCanvas object.
 * @author József Pollák
 */
public class PixelCanvasFactory {
    @Builder(
            builderMethodName = "simpleCanvas",
            buildMethodName = "build",
            builderClassName = "SimplePixelCanvasBuilder")
    private static PixelCanvas buildCanvas(double width, double height, int rows, int columns) {
        return new SimplePixelCanvas(width, height, rows, columns);
    }
}
