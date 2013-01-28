package fr.sylfrey.misTiGriD.environment;

/**
 * A service that provides a universal Time.
 * @author syl
 *
 */
public interface Time {
	
	/**
	 * @return how much time spent since the beginning of this day,
	 * on an arbitrary scale (cf. dayLength()).
	 */
	public long dayTime();
	
	/**
	 * @return the (constant) length of a day.
	 */
	public long dayLength();
	
}
