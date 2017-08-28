package informationStorage.config;

import core.Display;

/**
 * IDisplayConfig.java --- Configuration Interface for the {@link Display}
 * Class.
 * 
 * @author P H - 24.08.2017
 *
 */
public interface IDisplayConfig {

	/**
	 *
	 * @return true for enabling the highlight of the map's boundaries, false
	 *         for disabling it.
	 */
	public boolean enableDisplayMapBoundaries();

	/**
	 *
	 * @return true for enabling the highlight of the Polygons that represent
	 *         the reserved space on the map, false for disabling it.
	 */
	public boolean enableDisplayReservedSpacePolygons();

	/**
	 *
	 * @return true for enabling the highlight of the contended TilePositions,
	 *         false for disabling it.
	 */
	public boolean enableDisplayMapContendedTilePositions();

}
