package informationStorage.config;

import buildingOrderModule.BuildingOrderModule;
import buildingOrderModule.scoringDirector.gameState.GameState;

/**
 * IBuildingOrderModuleConfig.java --- Configuration Interface for the
 * {@link BuildingOrderModule} Class.
 * 
 * @author P H - 24.08.2017
 *
 */
public interface IBuildingOrderModuleConfig {

	/**
	 *
	 * @return true for enabling the generation of the updates on the
	 *         {@link BuildingOrderModule}, false for disabling them.
	 */
	public boolean enableBuildingOrderModuleUpdates();

	// TODO: UML ADD
	/**
	 *
	 * @return true for enabling the display of all {@link GameState}s with
	 *         their associated multipliers, false for disabling it.
	 */
	public boolean enableDisplayGameStates();

	// TODO: UML ADD
	/**
	 *
	 * @return true for enabling the display of all generated scores for each
	 *         available action, false for disabling it.
	 */
	public boolean enableDisplayGeneratedScores();
}
