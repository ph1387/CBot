package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.GameState;
import buildingOrderModule.simulator.TypeWrapper;
import bwapi.UnitType;

/**
 * TrainUnitActionTerran_SiegeTank.java --- Action for training a
 * Terran_SiegeTank.
 * 
 * @author P H - 30.04.2017
 *
 */
public class TrainUnitActionTerran_SiegeTank extends TrainUnitBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public TrainUnitActionTerran_SiegeTank(Object target) {
		super(target);

		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Expensive_Units);
		this.addToGameStates(GameState.Gas_Units);
		this.addToGameStates(GameState.Combat_Units);
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return UnitType.Terran_Siege_Tank_Tank_Mode;
	}

	@Override
	public TypeWrapper defineRequiredType() {
		return TypeWrapper.generateFrom(UnitType.Terran_Machine_Shop);
	}

}
