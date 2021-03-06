package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import buildingOrderModule.simulator.TypeWrapper;
import bwapi.UnitType;

/**
 * TrainUnitActionTerran_Goliath.java --- Class for training a Terran_Goliath.
 * 
 * @author P H - 22.09.2017
 *
 */
public class TrainUnitActionTerran_Goliath extends TrainUnitBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public TrainUnitActionTerran_Goliath(Object target) {
		super(target);

		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Expensive_Units);
		this.addToGameStates(GameState.Gas_Units);
		this.addToGameStates(GameState.Combat_Units);
		this.addToGameStates(GameState.Machine_Units);

		this.addToGameStates(GameState.FreeTrainingFacility_Terran_Factory);
		
		this.addToGameStates(GameState.SpecificUnit_Terran_Goliath);
	}

	// -------------------- Functions

	@Override
	public TypeWrapper defineRequiredType() {
		return TypeWrapper.generateFrom(UnitType.Terran_Factory);
	}

	@Override
	protected UnitType defineType() {
		return UnitType.Terran_Goliath;
	}

}
