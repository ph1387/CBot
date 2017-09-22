package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.UnitType;

// TODO: UML ADD
/**
 * ConstructActionTerran_ScienceFacilitiy.java --- Construction action for a
 * Terran_Science_Facility Unit.
 * 
 * @author P H - 22.09.2017
 *
 */
public class ConstructActionTerran_ScienceFacilitiy extends ConstructBaseAction {

	public ConstructActionTerran_ScienceFacilitiy(Object target) {
		super(target);

		this.addToGameStates(GameState.Building_Units);
		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Gas_Units);
		this.addToGameStates(GameState.Expensive_Units);
		this.addToGameStates(GameState.Technology_Focused);
		this.addToGameStates(GameState.Upgrade_Focused);
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return UnitType.Terran_Science_Facility;
	}

	// TODO: UML ADD
	@Override
	public int defineMaxSimulationOccurrences() {
		return 1;
	}

}