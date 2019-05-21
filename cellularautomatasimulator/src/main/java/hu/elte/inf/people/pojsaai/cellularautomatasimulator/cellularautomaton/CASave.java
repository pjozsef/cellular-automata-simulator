package hu.elte.inf.people.pojsaai.cellularautomatasimulator.cellularautomaton;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import hu.elte.inf.people.pojsaai.cellularautomaton.data.CAInitializer;
import hu.elte.inf.people.pojsaai.cellularautomaton.jobs.CAExecutor;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Data;

/**
 * The java class representing the contents of a save file.
 * This class is annotated with Jackson annotations, 
 * so it can be serialized and deserialised to/from JSON files.
 * @author József Pollák
 */
@Data
public class CASave {
    private String seed;
    private Map<String, String> graphicInit;
    private CAInitializer caInit;
    
    @JsonCreator
    public CASave(
            @JsonProperty("seed") String seed,
            @JsonProperty("graphicInit") Map<String, String> graphicInit,
            @JsonProperty("caInit") CAInitializer caInit) {
        this.seed = seed;
        this.graphicInit = graphicInit;
        this.caInit = caInit;
    }

    /**
     * A factory method that creates a CASave object from the given File.
     * @param file the source to read from
     * @return the deserialized initialization object
     * @throws IOException if file is not found
     */
    public static CASave create(File file) throws IOException {
        return create(file.toURI().toURL());
    }

    /**
     * A factory method that creates a CASave object from the given URL.
     * @param url the source to read from
     * @return the deserialized initialization object
     * @throws IOException if the URL is unreachable
     */
    public static CASave create(URL url) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(url, CASave.class);
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
                Logger.getLogger(CASave.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
}
