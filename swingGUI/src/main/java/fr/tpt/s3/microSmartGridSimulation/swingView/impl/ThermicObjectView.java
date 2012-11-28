package fr.tpt.s3.microSmartGridSimulation.swingView.impl;

import java.awt.Color;
import java.text.DecimalFormat;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import fr.tpt.s3.microSmartGridSimulation.environment.Updatable;
import fr.tpt.s3.microSmartGridSimulation.layout.Layout;
import fr.tpt.s3.microSmartGridSimulation.layout.ThermicObjectLayout;
import fr.tpt.s3.microSmartGridSimulation.swingView.SwingView;

@Component(name="ThermicObjectView",immediate=true)
@Provides(specifications={SwingView.class, Updatable.class})
public class ThermicObjectView extends LayoutView implements SwingView {

	@Requires(id="thermicObject")
	public ThermicObjectLayout thermicObject; 
	
	@Validate
	public void start() {
		panel.setBounds(x(), y(), width(), height());
		panel.setBorder(new BevelBorder(BevelBorder.RAISED));
		SimpleAttributeSet attribs = new SimpleAttributeSet();  
		StyleConstants.setAlignment(attribs , StyleConstants.ALIGN_RIGHT);  
		panel.setParagraphAttributes(attribs,true); 
		thermicObjectName = thermicObject.getName();
		update();
	}
	
	@Override
	public void update() {
		currentTemperature = thermicObject.getCurrentTemperature();
		SwingUtilities.invokeLater(updator);
	}
	
	@Override
	public Layout delegate() {
		return thermicObject;
	}
		
	@Invalidate
	public void stop() {
	}
	
	@Override
	public java.awt.Component component() {
		return panel;		
	}
	
	@Property(mandatory=true)
	public int period;

	@Override
	public int getPeriod() {
		return period;
	}
	
	public static Color color(float temperature) {
		return new Color( 
				250, 
				(int) Math.max(Math.min(
							Math.floor(250 - (temperature - 10)*16),
							250),
						0),
				0);
	}
	
	private JTextPane panel = new JTextPane();
	private float currentTemperature;
	private String thermicObjectName;
	private Runnable updator = new Runnable() {	@Override public void run() {
		panel.setBackground(color(currentTemperature));
		panel.setText(thermicObjectName + "\n" + df.format(currentTemperature) + " °C");
	}};

	private DecimalFormat df = new DecimalFormat("0.00");

}
