package hu.elte.inf.people.pojsaai.cellularautomaton.jobs;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import lombok.Value;

/**
 * This class handles the process of rendering and writing an in 
 * memory matrix to either a BufferedImage object or to an image File.
 * @author József Pollák
 */
@Value
public class ImageWriter implements Runnable {

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-LLLL-dd_HH:mm");
    private static final Calendar calendar = Calendar.getInstance();

    private final BufferedImage image;
    private final File path;
    
    private ImageWriter(BufferedImage image, File path){
        this.image=image;
        this.path=path;
    }

    @Override
    public void run() {
        try {
            ImageIO.write(image, "png", path);
        } catch (IOException ex) {
            Logger.getLogger(ImageWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Saves the parameter matrix to the File destination. 
     * The elements of the matrix will be mapped to an index in the Color array, 
     * so the Color array must be at least as large as the number of unique 
     * states in the parameter matrix. See: {@link hu.elte.inf.people.pojsaai.cellularautomaton.CellularAutomaton2D#countNumberOfStatesIn(int[][]) }
     * @param cachedCells the list of cellular automaton states
     * @param from beginning index of the list
     * @param to ending index of the list
     * @param colors the list of colors that determine the color of the image
     * @param pathBase the destination file
     */
    public static void saveImages(List<int[][]> cachedCells, int from, int to, Color[] colors, File pathBase) {
        if (to < from) {
            int temp = from;
            from = to;
            to = temp;
        }
        if (from < 0) {
            from = 0;
        }
        if (to > cachedCells.size()) {
            to = cachedCells.size();
        }

        for (int i = from; i <= to; i++) {
            saveImage(cachedCells.get(i), colors, pathBase);
        }
    }

    /**
     * Saves the parameter matrix to the File destination. 
     * The image will be scaled by the factor of scale. 
     * The elements of the matrix will be mapped to an index 
     * in the Color array, so the Color array must be at least 
     * as large as the number of unique states in the parameter matrix.
     * See: {@link hu.elte.inf.people.pojsaai.cellularautomaton.CellularAutomaton2D#countNumberOfStatesIn(int[][]) }
     * @param cells the matrix to save as image
     * @param colors colors the list of colors that determine the color of the image
     * @param pathBase the destination file
     */
    public static void saveImage(int[][] cells, Color[] colors, File pathBase) {
        saveImage(cells, colors, pathBase, 1);
    }

    /**
     * Saves the parameter matrix to the File destination. 
     * The image will be scaled by the factor of scale. 
     * The elements of the matrix will be mapped to an index 
     * in the Color array, so the Color array must be at least 
     * as large as the number of unique states in the parameter matrix.
     * See: {@link hu.elte.inf.people.pojsaai.cellularautomaton.CellularAutomaton2D#countNumberOfStatesIn(int[][]) }
     * @param cells the matrix to save as image
     * @param colors colors the list of colors that determine the color of the image
     * @param pathBase the destination file
     * @param scale the value the basic size is multiplied with
     */
    public static void saveImage(int[][] cells, Color[] colors, File pathBase, int scale) {
        ImageWriter writeJob = new ImageWriter(scaleImage(renderImage(cells, colors), scale), getTimestampedFile(pathBase, ".png"));
        CAExecutor.getInstance().submit(writeJob);
    }

    /**
     * Creates an array of BufferedImages from the supplied list of states
     * @param cachedCells the list of cellular automaton states
     * @param from beginning index of the list
     * @param to ending index of the list
     * @param colors the list of colors that determine the color of the image
     * @return the created BufferedImages
     */
    public static BufferedImage[] renderImages(List<int[][]> cachedCells, int from, int to, Color[] colors) {
        if (to < from) {
            int temp = from;
            from = to;
            to = temp;
        }
        if (from < 0) {
            from = 0;
        }
        if (to > cachedCells.size()) {
            to = cachedCells.size();
        }

        BufferedImage[] images = new BufferedImage[to - from];
        for (int i = from; i < to; i++) {
            images[i - from] = renderImage(cachedCells.get(i), colors);
        }
        return images;
    }

    /**
     * Creates BufferedImages from the supplied state
     * @param cells the cellular automaton state
     * @param colors the list of colors that determine the color of the image
     * @return the created BufferedImages
     */
    public static BufferedImage renderImage(int[][] cells, Color[] colors) {
        BufferedImage image = new BufferedImage(cells.length, cells[0].length, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < cells.length; ++i) {
            for (int j = 0; j < cells[i].length; j++) {
                int colorIndex = cells[i][j];
                if (colorIndex < 0 || colorIndex >= colors.length) {
                    throw new IllegalArgumentException(String.format("Color index in cells at (%d,%d) position is out of colors array index range: %d", i, j, colors.length));
                }
                Color color = colors[colorIndex];
                int alpha = 0xFF << 24;
                int red = (color.getRed() << 16) & 0x00FF0000;
                int green = (color.getGreen() << 8) & 0x0000FF00;
                int blue = color.getBlue();
                image.setRGB(i, j, alpha + red + green + blue);
            }
        }
        return image;
    }

    /**
     * Scales the supplied BufferedImage
     * @param input the source image
     * @param scale the factor for the scaling
     * @return 
     */
    public static BufferedImage scaleImage(BufferedImage input, int scale) {
        int width = input.getWidth();
        int height = input.getHeight();

        BufferedImage result = new BufferedImage(width * scale, height * scale, input.getType());

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int baseX = i * scale;
                int baseY = j * scale;
                for (int m = 0; m < scale; m++) {
                    for (int n = 0; n < scale; n++) {
                        result.setRGB(baseX + m, baseY + n, input.getRGB(i, j));
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns a File that has a timestamp in its filename
     * @param pathBase the prefix for the File
     * @return the timestamped File
     */
    public static File getTimestampedFile(File pathBase) {
        return getTimestampedFile(pathBase, "");
    }

    /**
     * Returns a File that has a timestamp in its filename
     * @param pathBase pathBase the prefix for the File
     * @param extension the extension for the returned File
     * @return the timestamped File
     */
    public static File getTimestampedFile(File pathBase, String extension) {
        String suffix = dateFormat.format(calendar.getTime());
        return new File(pathBase.toString() + "/" + suffix + "_" + System.currentTimeMillis() + extension);
    }
}
