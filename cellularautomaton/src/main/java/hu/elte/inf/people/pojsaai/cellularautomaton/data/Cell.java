package hu.elte.inf.people.pojsaai.cellularautomaton.data;

import lombok.Value;

/**
 * This immutable value class represents a cell of a cellular automaton.
 * @author József Pollák
 */
@Value
public class Cell {
    int x, y, state;
}
