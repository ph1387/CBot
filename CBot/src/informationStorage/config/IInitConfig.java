package informationStorage.config;

import core.Init;

/**
 * IInitConfig.java --- Configuration Interface for the {@link Init} Class.
 * 
 * @author P H - 24.08.2017
 *
 */
public interface IInitConfig {

	/**
	 * 
	 * @return true for enabling the generation of the default contended
	 *         TilePositions, false for disabling it.
	 */
	public boolean enableGenerateDefaultContendedTilePositions();

	/**
	 *
	 * @return true for enabling the generation of the default contended
	 *         Polygons (I.e. starting location, ChokePoints, ...).
	 */
	public boolean enableGenerateDefaultContendedPolygons();

	/**
	 *
	 * @return true for enabling the generation of the order in which the
	 *         Regions are accessed, false for disabling it.
	 */
	public boolean enableGenerateRegionAccessOrder();

}
