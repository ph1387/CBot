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
	 * @return true for enabling the updates on the {@link UnitTrackerModule},
	 *         false for disabling them.
	 */
	public boolean enableUnitTrackerModuleUpdates();

	/**
	 *
	 * @return true for enabling the highlight of the TilePositions containing
	 *         Player air strength influences, false for disabling it.
	 */
	public boolean enableDisplayPlayerAirStrength();

	/**
	 *
	 * @return true for enabling the highlight of the TilePositions containing
	 *         Player ground strength influences, false for disabling it.
	 */
	public boolean enableDisplayPlayerGroundStrength();

	/**
	 *
	 * @return true for enabling the highlight of the TilePositions containing
	 *         Player health strength influences, false for disabling it.
	 */
	public boolean enableDisplayPlayerHealthStrength();

	/**
	 *
	 * @return true for enabling the highlight of the TilePositions containing
	 *         Player support strength influences, false for disabling it.
	 */
	public boolean enableDisplayPlayerSupportStrength();

	/**
	 *
	 * @return true for enabling the highlight of the TilePositions containing
	 *         enemy Unit air strength influences, false for disabling it.
	 */
	public boolean enableDisplayEnemyAirStrength();

	/**
	 *
	 * @return true for enabling the highlight of the TilePositions containing
	 *         enemy Unit ground strength influences, false for disabling it.
	 */
	public boolean enableDisplayEnemyGroundStrength();

	/**
	 *
	 * @return true for enabling the highlight of the TilePositions containing
	 *         enemy Unit health strength influences, false for disabling it.
	 */
	public boolean enableDisplayEnemyHealthStrength();

	/**
	 *
	 * @return true for enabling the highlight of the TilePositions containing
	 *         enemy Unit support strength influences, false for disabling it.
	 */
	public boolean enableDisplayEnemySupportStrength();
}
