package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import buildingOrderModule.simulator.TypeWrapper;
import bwapi.UnitType;

/**
 * TrainUnitActionTerran_Vulture.java --- Class for training a Terran_Vulture.
 * 
 * @author P H - 31.08.2017
 *
 */
public class TrainUnitActionTerran_Vulture extends TrainUnitBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public TrainUnitActionTerran_Vulture(Object target) {
		super(target);

		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Cheap_Units);
		this.addToGameStates(GameState.Combat_Units);
		this.addToGameStates(GameState.Machine_Units);

		this.addToGameStates(GameState.FreeTrainingFacility_Terran_Factory);
		
		this.addToGameStates(GameState.SpecificUnit_Terran_Vulture);
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return UnitType.Terran_Vulture;
	}

	@Override
	public TypeWrapper defineRequiredType() {
		return TypeWrapper.generateFrom(UnitType.Terran_Factory);
	}

}
