package fr.sylfrey.misTiGriD.swingView.impl;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.border.BevelBorder;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import fr.sylfrey.misTiGriD.environment.Updatable;
import fr.sylfrey.misTiGriD.layout.Layout;
import fr.sylfrey.misTiGriD.layout.OpeningLayout;
import fr.sylfrey.misTiGriD.swingView.SwingView;

@Component(name="WallView")
@Provides(specifications={SwingView.class, Updatable.class})
public class WallView extends LayoutView implements SwingView {
	
	@Validate
	public void start() {
		button.setBounds(x(), y(), width(), height());
		button.setBorder(new BevelBorder(BevelBorder.RAISED));
		button.setBackground(Color.LIGHT_GRAY);
		button.addActionListener( new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				if (opening.isOpen()) {
					opening.close();
				} else {
					opening.open();
				}
				update();
		}});
		update();
	}

	@Override
	public void update() {
		if (opening.isOpen()) {
			button.setOpaque(false);
		} else {
			button.setOpaque(true);
		}
	}

	@Override
	public java.awt.Component component() {
		return button;		
	}
		
	@Override
	public int getPeriod() {
		return period;
	}
	
	
	@Requires(id="opening")
	public OpeningLayout opening; 

	@Property(mandatory=true)
	public int period;

	private JButton button = new JButton();

	@Override
	public Layout delegate() {
		return opening;
	}

}
