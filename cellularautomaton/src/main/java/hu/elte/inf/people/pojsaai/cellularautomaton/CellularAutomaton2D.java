package hu.elte.inf.people.pojsaai.cellularautomaton;

import static com.google.common.base.Preconditions.checkNotNull;
import hu.elte.inf.people.pojsaai.cellularautomaton.data.CAInitializer;
import hu.elte.inf.people.pojsaai.cellularautomaton.data.Cell;
import hu.elte.inf.people.pojsaai.cellularautomaton.data.Rule;
import hu.elte.inf.people.pojsaai.cellularautomaton.jobs.CAExecutor;
import hu.elte.inf.people.pojsaai.cellularautomaton.jobs.NextCellStateJob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * This class is the representation of a 2D, grid based cellular automaton. 
 * This class is immutable, all of its getters only return a defensive 
 * copy of the original value.
 * @author József Pollák
 */
@ToString
@EqualsAndHashCode
public final class CellularAutomaton2D {
    @Getter
    private int currentStateIndex;
    @Getter
    private final ExecutorService executor;

    private final long seed;
    private final int height, width, states;
    private final List<int[][]> cellCache;
    private final Map<Integer, List<Rule>> rules;
    private final Random random;

    public CellularAutomaton2D(CAInitializer init, ExecutorService executor) {
        checkNotNull(executor, "Executor parameter cannot be null!");
        checkNotNull(init.getCells(), "Cells member of CAInitializer cannot be null!");
        checkNotNull(init.getRules(), "Rules member of CAInitializer cannot be null!");

        int[][] tempCells = defensiveMatrixCopy(init.getCells());
        this.rules = defensiveMapCopy(init.getRules());
        this.seed = init.getSeed();

        currentStateIndex = 0;
        this.cellCache = new ArrayList<>(50);
        this.cellCache.add(tempCells);
        this.random = new Random(this.seed);
        this.width = tempCells.length;
        this.height = tempCells[0].length;
        this.executor = executor;
        this.states = this.rules.keySet().size();
    }

    public CellularAutomaton2D(CAInitializer init) {
        this(init, CAExecutor.getInstance());
    }

    /**
     * Calculates the next state of the automaton
     */
    public void nextState() {
        int[][] cells = getCurrentState();
        int[][] newCells = new int[width][height];
        for (int i = 0; i < cells.length; ++i) {
            for (int j = 0; j < cells[0].length; ++j) {
                newCells[i][j] = -6;
            }
        }
        List<Future<Cell>> futures = new ArrayList<>(width * height);
        for (int i = 0; i < cells.length; ++i) {
            for (int j = 0; j < cells[0].length; ++j) {
                List<Rule> transitionRules = rules.get(cells[i][j]);

                NextCellStateJob nextStateJob = NextCellStateJob
                        .builder()
                        .x(i)
                        .y(j)
                        .cells(cells)
                        .transitionRules(transitionRules)
                        .generatedRandom(random.nextDouble())
                        .build();
                futures.add(executor.submit(nextStateJob));

            }
        }
        futures.forEach(future -> {
            try {
                Cell result = future.get();
                int x = result.getX();
                int y = result.getY();
                int nextState = result.getState();
                newCells[x][y] = nextState;
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(CellularAutomaton2D.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        cellCache.add(newCells);
        currentStateIndex++;
    }

    /**
     * Returns the current state as a matrix. 
     * The returned value is a defensive copy of the original value.
     * @return the current state
     */
    public int[][] getCurrentState() {
        return defensiveMatrixCopy(cellCache.get(currentStateIndex));
    }

    /**
     * Returns the state at index i as a matrix.
     * The returned value is a defensive copy of the original value.
     * @param index the index of the state
     * @return the current state
     */
    public int[][] getState(int index) {
        return defensiveMatrixCopy(cellCache.get(index));
    }

    /**
     * Returns the initializer object of the cellular automaton. 
     * The returned value is a defensive copy of the original value.
     * @return the initializer object
     */
    public CAInitializer getInitializer() {
        return new CAInitializer(
                defensiveMatrixCopy(this.cellCache.get(0)),
                defensiveMapCopy(rules),
                seed);
    }
    
    /**
     * Returns the whole cache of the automaton. 
     * The returned list is an UnmodifiableList.
     * @return the cache of past states
     */
    public List<int[][]> getCellCache(){
        return Collections.unmodifiableList(cellCache);
    }

    /**
     * Counts the number of unique states in the given matrix.
     * @param cells the input matrix
     * @return the count of unique states in the matrix
     */
    public static int countNumberOfStatesIn(int[][] cells) {
        List<Integer> states = new ArrayList<>();
        for (int[] cell : cells) {
            for (int j = 0; j < cells[0].length; j++) {
                if (!states.contains(cell[j])) {
                    states.add(cell[j]);
                }
            }
        }
        return states.size();
    }

    private int[][] defensiveMatrixCopy(int[][] src) {
        int[][] defensive = new int[src.length][src[0].length];
        for (int i = 0; i < src.length; ++i) {
            System.arraycopy(src[i], 0, defensive[i], 0, src[0].length);
        }
        return defensive;

    }

    private Map<Integer, List<Rule>> defensiveMapCopy(Map<Integer, List<Rule>> src) {
        HashMap<Integer, List<Rule>> mapCopy = new HashMap<>();
        src.keySet().forEach((e) -> {
            List<Rule> listCopy = new ArrayList<>(src.get(e));
            mapCopy.put(e, listCopy);
        });
        return mapCopy;
    }
}
