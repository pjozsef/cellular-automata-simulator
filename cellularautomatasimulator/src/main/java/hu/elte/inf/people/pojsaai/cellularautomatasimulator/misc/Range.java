package hu.elte.inf.people.pojsaai.cellularautomatasimulator.misc;

import lombok.Value;

/**
 * A utility class that represents a numeric range.
 * @author József Pollák
 */
@Value
public class Range {
    private final int min;
    private final int max;
    
    public Range(int min, int max){
        if(min>max){
            throw new IllegalStateException("Min value cannot be greater than max value!");
        }else{
            this.min=min;
            this.max=max;
        }
    }
    
    /**
     * Checks if the given value is within the bounds of the range.
     * @param value the value to test
     * @return true if the given value is within the bounds of the range, otherwise false
     */
    public boolean withinRange(int value){
        return value>=min && value<=max;
    }
    
    /**
     * Returns a random number within the range.
     * @return the random number within the range
     */
    public int randomWithinRange(){
        return (int)(Math.random()*(max-min+1)+min);
    }
}
