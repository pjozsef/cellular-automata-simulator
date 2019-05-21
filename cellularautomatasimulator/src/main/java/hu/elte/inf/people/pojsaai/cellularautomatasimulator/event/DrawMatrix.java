package hu.elte.inf.people.pojsaai.cellularautomatasimulator.event;

import lombok.Value;

/**
 * Event used for drawing a matrix on the PixelCanvas.
 * @author József Pollák
 */
@Value
public class DrawMatrix {
    int index;
}
