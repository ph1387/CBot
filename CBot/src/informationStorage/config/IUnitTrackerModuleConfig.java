package informationStorage.config;

import unitTrackerModule.UnitTrackerModule;

/**
 * IUnitTrackerModuleConfig.java --- Configuration Interface for the
 * {@link UnitTrackerModule} Class.
 * 
 * @author P H - 24.08.2017
 *
 */
public interface IUnitTrackerModuleConfig {

	/**
	 *
	 * @return true for enabling the highlight of the TilePositions containing
	 *         Player strength influences, false for disabling it.
	 */
	public boolean enableDisplayPlayerStrength();

	/**
	 *
	 * @return true for enabling the highlight of the TilePositions containing
	 *         enemy Unit strength influences, false for disabling it.
	 */
	public boolean enableDisplayEnemyStrength();
}
