/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.elte.inf.people.pojsaai.cellularautomatasimulator.event;

import lombok.Value;

/**
 * Event used for increasing the range in the state browsing slider.
 * @author József Pollák
 */
@Value
public class IncreaseSliderRange {
    int newMax;
}
