package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import buildingOrderModule.simulator.TypeWrapper;
import bwapi.UnitType;

// TODO: UML ADD
/**
 * TrainUnitActionTerran_Wraith.java --- Class for training a Terran_Wraith.
 * 
 * @author P H - 19.09.2017
 *
 */
public class TrainUnitActionTerran_Wraith extends TrainUnitBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public TrainUnitActionTerran_Wraith(Object target) {
		super(target);

		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Gas_Units);
		this.addToGameStates(GameState.Expensive_Units);
		this.addToGameStates(GameState.Combat_Units);
		this.addToGameStates(GameState.Flying_Units);

		this.addToGameStates(GameState.FreeTrainingFacility_Terran_Starport);
	}

	// -------------------- Functions

	@Override
	public TypeWrapper defineRequiredType() {
		return TypeWrapper.generateFrom(UnitType.Terran_Starport);
	}

	@Override
	protected UnitType defineType() {
		return UnitType.Terran_Wraith;
	}

}
