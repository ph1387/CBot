package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.UnitType;

// TODO: WIP
// TODO: UML ADD
/**
 * ConstructActionTerran_Refinery.java --- Action for constructing a
 * Terran_Refinery.
 * 
 * @author P H - 13.03.2018
 *
 */
public class ConstructActionTerran_Refinery extends ConstructActionRefinery {

	/**
	 * @param target
	 *            type: Integer
	 */
	public ConstructActionTerran_Refinery(Object target) {
		super(target);

		this.addToGameStates(GameState.SpecificBuilding_Terran_Refinery);
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return UnitType.Terran_Refinery;
	}

}
