package fr.tpt.s3.microSmartGridSimulation.layout;

import fr.tpt.s3.microSmartGridSimulation.appliances.Heater;

/**
 * Describes a Heater that has a certain size and position (cf. Layout)
 * a temperature (cf. ThermicObjectLayout) and a power parameter (cf. Heater).
 * @author syl
 */
public interface HeaterLayout extends ThermicObjectLayout, Heater {

}
