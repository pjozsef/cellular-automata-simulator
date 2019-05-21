package hu.elte.inf.people.pojsaai.cellularautomatasimulator.event;

import lombok.Value;

/**
 * Event used for enabling/disabling the EditPane mouse listeneres.
 * @author József Pollák
 */
@Value
public class DeactivateEditPaneAnimation {
    boolean deactivated;
}
