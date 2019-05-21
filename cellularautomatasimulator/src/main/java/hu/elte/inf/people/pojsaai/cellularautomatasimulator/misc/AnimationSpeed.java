package hu.elte.inf.people.pojsaai.cellularautomatasimulator.misc;

/**
 * An enum, containing various animation speeds used in the application.
 * @author József Pollák
 */
public enum AnimationSpeed {

    ANIMATION_MEDIUM(500),
    ANIMATION_FAST(200),
    ANIMATION_BLAZING_FAST(50);

    private final double speed;

    private AnimationSpeed(double speed) {
        this.speed = speed;
    }

    public double speed() {
        return speed;
    }
}
