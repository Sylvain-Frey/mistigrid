package fr.sylfrey.misTiGriD.swingView.impl;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;

import fr.sylfrey.misTiGriD.swingView.SwingView;

@Component(name="LocalView",immediate=true)
public class LocalView extends JFrame {

	@Validate
	public void start() {

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(x, y, width, height);
		contentPane.setPreferredSize(new Dimension(width, height));
		contentPane.setOpaque(true);
		setContentPane(new JScrollPane(contentPane));
		pack();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					LocalView.this.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Invalidate
	public void stop() {
		dispose();
	}

	@Bind(specification="fr.sylfrey.misTiGriD.swingView.SwingView",aggregate=true)
	public void bind(SwingView view) {
		contentPane.add(view.component(), new Integer(view.layer())	);
	}

	@Unbind(specification="fr.sylfrey.misTiGriD.swingView.SwingView",aggregate=true)
	public void unbind(SwingView view) {
		contentPane.remove(view.component());
	}

	@Property
	public int x;

	@Property
	public int y;

	@Property
	public int width;

	@Property
	public int height;

	private JLayeredPane contentPane = new JLayeredPane();
	private static final long serialVersionUID = 1L;

}
