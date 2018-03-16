package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.UnitType;

// TODO: UML ADD
/**
 * ConstructActionTerran_Bunker.java --- Construction action for a Terran_Bunker
 * Unit.
 * 
 * @author P H - 16.03.2018
 *
 */
public class ConstructActionTerran_Bunker extends ConstructBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public ConstructActionTerran_Bunker(Object target) {
		super(target);

		this.addToGameStates(GameState.Building_Units);
		this.addToGameStates(GameState.Combat_Units);
		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Expensive_Units);

		this.addToGameStates(GameState.SpecificBuilding_Terran_Bunker);
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return UnitType.Terran_Bunker;
	}

	@Override
	public int defineMaxSimulationOccurrences() {
		return 1;
	}

}
