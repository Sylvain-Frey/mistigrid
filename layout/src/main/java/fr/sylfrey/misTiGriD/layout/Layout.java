package fr.sylfrey.misTiGriD.layout;

/**
 * Describes a two-dimensional rectangular object of size (width,height)
 * at position (x,y). Stacking overlapping Layouts depend on their layer
 * attribute: the higher the layer, the higher the Layout is in a stack.
 * Note: usually, the (x,y) axis follow screen display convention:
 * (0,0) is the top left corner, x axis to the right, y axis to the bottom.
 * Note: the size unit is arbitrary: it might be in pixels, meters, etc. 
 * @author syl
 *
 */
public interface Layout {
	
	/**
	 * @return unique identifier of this Layout
	 */
	public String name();

	/**
	 * @return abscissa, from left to right, 
	 * of the top left corner of this Layout.
	 */
	public int x();
	
	/**
	 * @return ordinate, from top to bottom, 
	 * of the top left corner of this Layout.
	 */
	public int y();
	
	/**
	 * @return width of this Layout; for instance,
	 * top-right corner's abscissa is this.x() + this.width().
	 */
	public int width();

	/**
	 * @return height of this Layout; for instance,
	 * bottom corner's ordinate is this.y() + this.height().
	 */	
	public int height();
	
	/**
	 * @return the layer on which to display this Layout;
	 * considering Layouts l1 and l2 with l1.layer() > l2.layer(),
	 * l1 will be displayed above l2.
	 */
	public int layer();
	
}
