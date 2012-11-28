package fr.tpt.s3.microSmartGridSimulation.swingView.impl;

import javax.swing.BorderFactory;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import fr.tpt.s3.microSmartGridSimulation.environment.Updatable;
import fr.tpt.s3.microSmartGridSimulation.layout.Layout;
import fr.tpt.s3.microSmartGridSimulation.layout.ProsumerLayout;
import fr.tpt.s3.microSmartGridSimulation.swingView.SwingView;

@Component(name="SolarPanelView",immediate=true)
@Provides(specifications={SwingView.class, Updatable.class})
public class SolarPanelView extends LayoutView implements SwingView {
		
	@Validate
	public void start() {
		textPanel.setBounds(x(), y(), width(), height());
		textPanel.setBorder(new CompoundBorder(
				new BevelBorder(BevelBorder.RAISED),
				BorderFactory.createTitledBorder("<html><font size=\"2\">Solar Panel</html>")));
	}
	
	@Override
	public int getPeriod() {
		return period;
	}

	@Override
	public void update() {
		power = solarPanel.getProsumedPower();
		SwingUtilities.invokeLater(updator);
	}

	@Override
	public java.awt.Component component() {
		return textPanel;
	}
	
	@Override
	public Layout delegate() {
		return solarPanel;
	}

	
	
	@Requires(id="solarPanel")
	public ProsumerLayout solarPanel;
	

	@Property(mandatory=true)
	public int period;	
	
	
	private JTextPane textPanel = new JTextPane();
	private float power;
	private Runnable updator = new Runnable() {	@Override public void run() {
		textPanel.setText(power + " W");
	}};
	
}
