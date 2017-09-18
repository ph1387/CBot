package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.UnitType;

/**
 * ConstructActionTerran_Barracks.java --- Construction action for a
 * Terran_Barracks Unit.
 * 
 * @author P H - 30.04.2017
 *
 */
public class ConstructActionTerran_Barracks extends ConstructBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public ConstructActionTerran_Barracks(Object target) {
		super(target);

		this.addToGameStates(GameState.Building_Units);
		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Expensive_Units);
		this.addToGameStates(GameState.Bio_Units);

		this.addToGameStates(GameState.IdleTrainingFacility_Terran_Barracks);
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return UnitType.Terran_Barracks;
	}

}
