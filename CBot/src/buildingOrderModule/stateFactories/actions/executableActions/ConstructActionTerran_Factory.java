package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.UnitType;

/**
 * ConstructActionTerran_Factory.java --- Construction action for a
 * Terran_Factory Unit.
 * 
 * @author P H - 30.04.2017
 *
 */
public class ConstructActionTerran_Factory extends ConstructBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public ConstructActionTerran_Factory(Object target) {
		super(target);

		this.addToGameStates(GameState.Building_Units);
		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Gas_Units);
		this.addToGameStates(GameState.Expensive_Units);
		this.addToGameStates(GameState.Machine_Units);

		this.addToGameStates(GameState.IdleTrainingFacility_Terran_Factory);
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return UnitType.Terran_Factory;
	}

	@Override
	public int defineMaxSimulationOccurrences() {
		return 2;
	}
}
