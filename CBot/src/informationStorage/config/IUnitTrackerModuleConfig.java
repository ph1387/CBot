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

	// TODO: UML REMOVE
	// /**
	// *
	// * @return true for enabling the highlight of the TilePositions containing
	// * Player strength influences, false for disabling it.
	// */
	// public boolean enableDisplayPlayerStrength();

	// TODO: UML ADD
	/**
	 *
	 * @return true for enabling the highlight of the TilePositions containing
	 *         Player air strength influences, false for disabling it.
	 */
	public boolean enableDisplayPlayerAirStrength();

	// TODO: UML ADD
	/**
	 *
	 * @return true for enabling the highlight of the TilePositions containing
	 *         Player ground strength influences, false for disabling it.
	 */
	public boolean enableDisplayPlayerGroundStrength();

	// TODO: UML ADD
	/**
	 *
	 * @return true for enabling the highlight of the TilePositions containing
	 *         Player health strength influences, false for disabling it.
	 */
	public boolean enableDisplayPlayerHealthStrength();

	// TODO: UML ADD
	/**
	 *
	 * @return true for enabling the highlight of the TilePositions containing
	 *         Player support strength influences, false for disabling it.
	 */
	public boolean enableDisplayPlayerSupportStrength();

	// TODO: UML REMOVE
	// /**
	// *
	// * @return true for enabling the highlight of the TilePositions containing
	// * enemy Unit strength influences, false for disabling it.
	// */
	// public boolean enableDisplayEnemyStrength();

	// TODO: UML ADD
	/**
	 *
	 * @return true for enabling the highlight of the TilePositions containing
	 *         enemy Unit air strength influences, false for disabling it.
	 */
	public boolean enableDisplayEnemyAirStrength();

	// TODO: UML ADD
	/**
	 *
	 * @return true for enabling the highlight of the TilePositions containing
	 *         enemy Unit ground strength influences, false for disabling it.
	 */
	public boolean enableDisplayEnemyGroundStrength();

	// TODO: UML ADD
	/**
	 *
	 * @return true for enabling the highlight of the TilePositions containing
	 *         enemy Unit health strength influences, false for disabling it.
	 */
	public boolean enableDisplayEnemyHealthStrength();

	// TODO: UML ADD
	/**
	 *
	 * @return true for enabling the highlight of the TilePositions containing
	 *         enemy Unit support strength influences, false for disabling it.
	 */
	public boolean enableDisplayEnemySupportStrength();
}
