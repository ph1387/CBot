package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.UnitType;

/**
 * ConstructActionTerran_Starport.java --- Construction action for a
 * Terran_Starport Unit.
 * 
 * @author P H - 19.09.2017
 *
 */
public class ConstructActionTerran_Starport extends ConstructBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public ConstructActionTerran_Starport(Object target) {
		super(target);

		this.addToGameStates(GameState.Building_Units);
		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Gas_Units);
		this.addToGameStates(GameState.Expensive_Units);
		this.addToGameStates(GameState.Flying_Units);

		this.addToGameStates(GameState.IdleTrainingFacility_Terran_Starport);
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return UnitType.Terran_Starport;
	}

}
