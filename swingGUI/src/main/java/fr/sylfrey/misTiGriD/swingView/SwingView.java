package fr.sylfrey.misTiGriD.swingView;

import java.awt.Component;

import fr.sylfrey.misTiGriD.environment.Updatable;
import fr.sylfrey.misTiGriD.layout.Layout;

/**
 * Swing wrapper for a Layout.
 * @author syl
 *
 */
public interface SwingView extends Updatable, Layout {
	
	public Component component();

}
