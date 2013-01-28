package fr.sylfrey.misTiGriD.swingView.impl;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import fr.sylfrey.misTiGriD.electricalGrid.Storage.State;
import fr.sylfrey.misTiGriD.environment.Updatable;
import fr.sylfrey.misTiGriD.layout.Layout;
import fr.sylfrey.misTiGriD.layout.StorageLayout;
import fr.sylfrey.misTiGriD.swingView.SwingView;

@Component(name="StorageView")
@Provides(specifications={SwingView.class, Updatable.class})
public class StorageView extends LayoutView implements SwingView {

	@Validate
	public void start() {

		loadSlider = new JSlider(JSlider.VERTICAL, 0, 100, 0);
		loadSlider.setMinorTickSpacing(100/4);
		loadSlider.setPaintTicks(true);

		loadButton.addActionListener(new ActionListener() {			
			@Override public void actionPerformed(ActionEvent e) {
				storage.setState(State.LOADING);
			}
		});

		standbyButton.addActionListener(new ActionListener() {			
			@Override public void actionPerformed(ActionEvent e) {
				storage.setState(State.STANDBY);
			}
		});

		unloadButton.addActionListener(new ActionListener() {			
			@Override public void actionPerformed(ActionEvent e) {
				storage.setState(State.UNLOADING);
			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(3,1));
		buttonPanel.add(loadButton);
		buttonPanel.add(standbyButton);
		buttonPanel.add(unloadButton);

		panel = new JPanel();
		panel.setBounds(x(), y(), width(), height());
		panel.setLayout(new BorderLayout());
		panel.add(buttonPanel, BorderLayout.CENTER);
		panel.add(loadSlider, BorderLayout.EAST);
		panel.setBorder(new CompoundBorder(
				new BevelBorder(BevelBorder.RAISED),
				BorderFactory.createTitledBorder("<html><font size=\"2\">storage</html>")));

		update();
	}	

	@Override
	public void update() {
		newLoadRate = (int)((storage.getLoad()/storage.getLoadCapacity())*100);
		newCurrentState = storage.getState();	
		SwingUtilities.invokeLater(updator);
	}

	@Override
	public java.awt.Component component() {
		return panel;		
	}

	@Override
	public Layout delegate() {
		return storage;
	}

	@Override
	public int getPeriod() {
		return period;
	}

	
	@Property(mandatory=true)
	public int period;
	
	@Requires
	public StorageLayout storage;


	private JPanel panel;
	private JSlider loadSlider;
	private JButton loadButton = new JButton("<html><font size=\"1\">load</html>");
	private JButton standbyButton = new JButton("<html><font size=\"1\">standby</html>");
	private JButton unloadButton = new JButton("<html><font size=\"1\">unload</html>");

	private int loadRate;
	private int newLoadRate;
	private State currentState;
	private State newCurrentState;
	private Runnable updator = new Runnable() {	@Override public void run() {
		if (newLoadRate!=loadRate) {
			loadSlider.setValue(newLoadRate);
			loadRate = newLoadRate;
		}
		if (newCurrentState!=currentState) {
			currentState = newCurrentState;
			switch (storage.getState()) {
			case LOADING:
				loadButton.setEnabled(false);
				standbyButton.setEnabled(true);
				unloadButton.setEnabled(true);
				break;
			case STANDBY:
				loadButton.setEnabled(true);
				standbyButton.setEnabled(false);
				unloadButton.setEnabled(true);
				break;
			case UNLOADING:
				loadButton.setEnabled(true);
				standbyButton.setEnabled(true);
				unloadButton.setEnabled(false);
				break;		
			}
		}
	}};
	
}
