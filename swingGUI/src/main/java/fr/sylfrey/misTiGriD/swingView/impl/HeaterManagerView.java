package fr.sylfrey.misTiGriD.swingView.impl;

import java.awt.Color;
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
import fr.sylfrey.misTiGriD.layout.HeaterManagerLayout;
import fr.sylfrey.misTiGriD.layout.Layout;
import fr.sylfrey.misTiGriD.layout.ThermicObjectLayout;
import fr.sylfrey.misTiGriD.swingView.SwingView;

@Component(name="HeaterManagerView")
@Provides(specifications={SwingView.class, Updatable.class})
public class HeaterManagerView extends LayoutView implements SwingView {

	@Validate
	public void start() {
		button.setBounds(x(), y(), width(), height());	
		button.setBorder(new BevelBorder(BevelBorder.RAISED));	
		button.addActionListener(new ActionListener() {			
			@Override public void actionPerformed(ActionEvent e) {
				String input = JOptionPane.showInputDialog(
						HeaterManagerView.this.button, 
						"New required temperature (°C):",
						"HeaterManager Configuration",
						JOptionPane.PLAIN_MESSAGE);
				if (input!=null && !input.isEmpty()) {
					float newTemp = Float.valueOf(input);
					requiredTemperature = newTemp;
					manager.setRequiredTemperature(newTemp);
				}
			}
		});
		update();
	}
	

	@Override
	public void update() {
		requiredTemperature = manager.getRequiredTemperature();
		currentTemperature = room.getCurrentTemperature();
		goalReached = requiredTemperature - 0.1 < currentTemperature;
		goalOvershoot = requiredTemperature + 2 < currentTemperature;
		SwingUtilities.invokeLater(updator);
	}

	@Override
	public Layout delegate() {
		return manager;
	}

	@Override
	public int getPeriod() {
		return period;
	}

	@Override
	public java.awt.Component component() {
		return button;		
	}
	
	
	@Requires(id="manager")
	public HeaterManagerLayout manager; 
	
	@Requires(id="room")
	public ThermicObjectLayout room; 

	@Property(mandatory=true)
	public int period;
	
	
	private JButton button = new JButton();
	private float requiredTemperature;
	private float currentTemperature;
	private boolean goalReached;
	private boolean goalOvershoot;
	private Runnable updator = new Runnable() {	@Override public void run() {
		button.setText("<html><font size=\"2\">" + df.format(currentTemperature) + "°C<br>req: "
				+ df.format(requiredTemperature) + " °C</html>");
		if(goalReached) {
			if (goalOvershoot) {
				button.setBackground(Color.RED);
			} else {
				button.setBackground(Color.GREEN);
			}			
		} else {
			button.setBackground(AZURE);			
		}
	}};
	private final Color AZURE = new Color(0,127,255);
	
	private DecimalFormat df = new DecimalFormat("0.00");

}
