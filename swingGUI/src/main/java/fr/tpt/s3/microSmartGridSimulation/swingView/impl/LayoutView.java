package fr.tpt.s3.microSmartGridSimulation.swingView.impl;

import java.awt.Component;

import fr.tpt.s3.microSmartGridSimulation.layout.Layout;
import fr.tpt.s3.microSmartGridSimulation.swingView.SwingView;

public abstract class LayoutView implements SwingView {

	public int x() {
		return delegate().x();
	}

	public int y() {
		return delegate().y();
	}

	public int width() {
		return delegate().width();
	}

	public int height() {
		return delegate().height();
	}

	public int layer() {
		return delegate().layer();
	}
	
	abstract public Layout delegate();

	abstract public int getPeriod();
	
	abstract public void update();

	abstract public Component component();


}
