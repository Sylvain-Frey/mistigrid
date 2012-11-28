package fr.tpt.s3.microSmartGridSimulation.layout;

import fr.tpt.s3.microSmartGridSimulation.temperature.Opening;

/**
 * Describes a Wall with a certain size and position (cf. Layout)
 * and possibly an Opening that can be opened, closed (cf. Opening).
 * @author syl
 */
public interface OpeningLayout extends Layout, Opening {

}
