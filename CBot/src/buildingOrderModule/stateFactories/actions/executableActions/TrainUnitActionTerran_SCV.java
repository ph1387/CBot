package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import buildingOrderModule.simulator.TypeWrapper;
import bwapi.UnitType;

/**
 * TrainUnitActionTerran_SCV.java --- Action for training a Terran_SCV.
 * 
 * @author P H - 13.03.2018
 *
 */
public class TrainUnitActionTerran_SCV extends TrainUnitActionWorker {

	/**
	 * @param target
	 *            type: Integer
	 */
	public TrainUnitActionTerran_SCV(Object target) {
		super(target);

		this.addToGameStates(GameState.FreeTrainingFacility_Terran_CommandCenter);

		this.addToGameStates(GameState.SpecificUnit_Terran_SCV);
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return UnitType.Terran_SCV;
	}

	@Override
	public TypeWrapper defineRequiredType() {
		return TypeWrapper.generateFrom(UnitType.Terran_Command_Center);
	}

}
