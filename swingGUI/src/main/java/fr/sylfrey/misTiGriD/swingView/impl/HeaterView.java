package fr.sylfrey.misTiGriD.swingView.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import fr.sylfrey.misTiGriD.environment.Updatable;
import fr.sylfrey.misTiGriD.layout.HeaterLayout;
import fr.sylfrey.misTiGriD.layout.Layout;
import fr.sylfrey.misTiGriD.swingView.SwingView;

@Component(name="HeaterView")
@Provides(specifications={SwingView.class, Updatable.class})
public class HeaterView extends LayoutView implements SwingView {
	
	@Validate
	public void start() {
		button.setBounds(x(), y(), width(), height());	
		button.setBorder(new BevelBorder(BevelBorder.RAISED));	
		button.addActionListener(new ActionListener() {			
			@Override public void actionPerformed(ActionEvent e) {
				String input = JOptionPane.showInputDialog(
						HeaterView.this.button, 
						"New emission power (W):",
						"Heater Configuration",
						JOptionPane.PLAIN_MESSAGE);
				if (input!=null && !input.isEmpty()) {
					heater.setEmissionPower(Integer.decode(input));
				}
			}
		});
		heaterName = heater.getName();
		update();
	}
	
	@Override
	public void update() {
		heaterTemperature = heater.getCurrentTemperature();
		heaterPower = heater.getProsumedPower();
		SwingUtilities.invokeLater(updator);
	}

	@Override
	public java.awt.Component component() {
		return button;		
	}
	
	@Override
	public Layout delegate() {
		return heater;
	}

	@Override
	public int getPeriod() {
		return period;
	}
	
	
	@Requires(id="heater")
	public HeaterLayout heater; 

	@Property(mandatory=true)
	public int period;


	
	private JButton button = new JButton();
	
	private String heaterName;
	private float heaterTemperature;
	private float heaterPower;
	private Runnable updator = new Runnable() {	@Override public void run() {
		button.setText("<html><font size=\"2\">" + heaterName + "<br>"
				+ fW.format(heaterPower) + " W<br>" 
				+ fT.format(heaterTemperature) + " °C</html>");
		button.setBackground(ThermicObjectView.color(heaterTemperature));
	}};
	
	private DecimalFormat fT = new DecimalFormat("0.00");
	private DecimalFormat fW = new DecimalFormat("0");
	
}
