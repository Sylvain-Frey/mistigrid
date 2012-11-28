package fr.tpt.s3.microSmartGridSimulation.swingView;

import java.awt.Component;

import fr.tpt.s3.microSmartGridSimulation.environment.Updatable;
import fr.tpt.s3.microSmartGridSimulation.layout.Layout;

/**
 * Swing wrapper for a Layout.
 * @author syl
 *
 */
public interface SwingView extends Updatable, Layout {
	
	public Component component();

}
