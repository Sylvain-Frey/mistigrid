package fr.sylfrey.misTiGriD.layout;

import fr.sylfrey.misTiGriD.temperature.Opening;

/**
 * Describes a Wall with a certain size and position (cf. Layout)
 * and possibly an Opening that can be opened, closed (cf. Opening).
 * @author syl
 */
public interface OpeningLayout extends Layout, Opening {

}
