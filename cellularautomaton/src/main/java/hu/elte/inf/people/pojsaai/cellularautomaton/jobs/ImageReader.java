package hu.elte.inf.people.pojsaai.cellularautomaton.jobs;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 * This utility class provides methods to create random matrices and read matrices from images.
 * @author József Pollák
 */
public class ImageReader{

    private ImageReader(){
    }

    /**
     * Reads the file and returns the parsed matrix.
     * @param inputFile the source to read from
     * @return the parse matrix
     * @throws IOException if the file is not found
     */
    public static int[][] readCellsFromImage(File inputFile) throws IOException{
        BufferedImage image = ImageIO.read(inputFile);
        final int width = image.getWidth();
        final int height = image.getHeight();

        Map<Integer, Integer> colorMap = new HashMap<>();
        int index = 0;

        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                int color = image.getRGB(i, j);
                if(colorMap.get(color) == null){
                    colorMap.put(color, index);
                    index++;
                }
            }
        }

        int[][] cells = new int[width][height];

        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                int key = image.getRGB(i, j);
                cells[i][j] = colorMap.get(key);
            }
        }

        return cells;
    }

    /**
     * Reads the file represented by the String parameter and returns the parsed matrix.
     * @param inputString the String representation of the source file path to read from
     * @return the parse matrix
     * @throws IOException if the file is not found
     */
    public static int[][] readCellsFromImage(String inputString) throws IOException{
        return readCellsFromImage(new File(inputString));
    }
    
    /**
     * Returns a randomly populated matrix.
     * @param width width of the matrix
     * @param height height of the matrix
     * @param numberOfStates possible unique states in the matrix
     * @return the random matrix
     */
    public static int[][] getRandomImage(int width, int height, int numberOfStates){
        return getRandomImage(width, height, numberOfStates, new Random().nextLong());
    }
    
    /**
     * Returns a randomly populated matrix.
     * @param width width of the matrix
     * @param height height of the matrix
     * @param numberOfStates possible unique states in the matrix
     * @param seed the seed value used for the random generator
     * @return the random matrix
     */
    public static int[][] getRandomImage(int width, int height, int numberOfStates, long seed){
        Random random = new Random(seed);
        int[][] randomImage = new int[width][height];
        for(int i=0; i<width; ++i){
            for(int j=0; j<height; ++j){
                randomImage[i][j] = Math.abs(random.nextInt())%numberOfStates;
            }
        }
        return randomImage;
    }
}
