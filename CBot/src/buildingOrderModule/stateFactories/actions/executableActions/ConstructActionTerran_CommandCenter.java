package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.UnitType;

/**
 * ConstructActionTerran_CommandCenter.java --- Action for constructing a
 * Terran_Command_Center.
 * 
 * @author P H - 13.03.2018
 *
 */
public class ConstructActionTerran_CommandCenter extends ConstructActionCenter {

	/**
	 * @param target
	 *            type: Integer
	 */
	public ConstructActionTerran_CommandCenter(Object target) {
		super(target);

		this.addToGameStates(GameState.IdleTrainingFacility_Terran_CommandCenter);

		this.addToGameStates(GameState.SpecificBuilding_Terran_Command_Center);
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return UnitType.Terran_Command_Center;
	}

}
