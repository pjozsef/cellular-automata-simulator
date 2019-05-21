package hu.elte.inf.people.pojsaai.cellularautomaton.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import hu.elte.inf.people.pojsaai.cellularautomaton.jobs.CAExecutor;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Data;

/**
 * This class represents all the necessary information required for a 
 * CellularAutomaton2D object to be instantiated. It contains the 
 * initial cell states, the transition rules and the random seed. 
 * This class is mutable. This class is annotated with Jackson annotations, 
 * so it can be serialized and deserialised to/from JSON files.
 * @author József Pollák
 */
@Data
public class CAInitializer {

    private int[][] cells;
    private Map<Integer, List<Rule>> rules;
    private long seed;

    @JsonCreator
    public CAInitializer(
            @JsonProperty("cells") int[][] cells,
            @JsonProperty("rules") Map<Integer, List<Rule>> rules,
            @JsonProperty("seed") long seed) {
        this.cells = cells;
        this.rules = rules;
        this.seed = seed;
    }

    /**
     * A factory method that creates a CAInitializer object from the given File.
     * @param file the source to read from
     * @return the deserialized initialization object
     * @throws IOException if file is not found
     */
    public static CAInitializer create(File file) throws IOException {
        return create(file.toURI().toURL());
    }
 
    /**
     * A factory method that creates a CAInitializer object from the given URL.
     * @param url the source to read from
     * @return the deserialized initialization object
     * @throws IOException if the URL is unreachable
     */
    public static CAInitializer create(URL url) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(url, CAInitializer.class);
    }

    /**
     * Serializes this object into a JSON file.
     * @param file the file destination
     */
    public void save(File file) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        CAExecutor.getInstance().submit(() -> {
            try {
                mapper.writerWithDefaultPrettyPrinter()
                        .writeValue(file, this);
            } catch (IOException ex) {
                Logger.getLogger(CAInitializer.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
}
