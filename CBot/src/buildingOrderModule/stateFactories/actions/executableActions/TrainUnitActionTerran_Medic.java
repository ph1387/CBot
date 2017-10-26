package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import buildingOrderModule.simulator.TypeWrapper;
import bwapi.UnitType;

/**
 * TrainUnitActionTerran_Medic.java --- Class for training a Terran_Medic.
 * 
 * @author P H - 19.09.2017
 *
 */
public class TrainUnitActionTerran_Medic extends TrainUnitBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public TrainUnitActionTerran_Medic(Object target) {
		super(target);

		this.addToGameStates(GameState.Cheap_Units);
		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Bio_Units);
		this.addToGameStates(GameState.Healer_Units);

		this.addToGameStates(GameState.FreeTrainingFacility_Terran_Barracks);
		
		this.addToGameStates(GameState.SpecificUnit_Terran_Medic);
	}

	// -------------------- Functions

	@Override
	public TypeWrapper defineRequiredType() {
		return TypeWrapper.generateFrom(UnitType.Terran_Academy);
	}

	@Override
	protected UnitType defineType() {
		return UnitType.Terran_Medic;
	}

}
