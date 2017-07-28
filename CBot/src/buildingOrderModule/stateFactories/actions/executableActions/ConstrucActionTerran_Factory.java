package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.GameState;
import bwapi.UnitType;

/**
 * ConstrucActionTerran_Factory.java --- Construction action for a
 * Terran_Factory Unit.
 * 
 * @author P H - 30.04.2017
 *
 */
public class ConstrucActionTerran_Factory extends ConstructBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public ConstrucActionTerran_Factory(Object target) {
		super(target);

		this.addToGameStates(GameState.Building_Units);
		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Gas_Units);
		this.addToGameStates(GameState.Expensive_Units);
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return UnitType.Terran_Factory;
	}

}
