package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import buildingOrderModule.simulator.TypeWrapper;
import bwapi.UnitType;

/**
 * TrainUnitActionTerran_Marine.java --- Class for training a Terran_Marine.
 * 
 * @author P H - 29.04.2017
 *
 */
public class TrainUnitActionTerran_Marine extends TrainUnitBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public TrainUnitActionTerran_Marine(Object target) {
		super(target);

		this.addToGameStates(GameState.Cheap_Units);
		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Bio_Units);
		this.addToGameStates(GameState.Combat_Units);
		
		this.addToGameStates(GameState.FreeTrainingFacility_Terran_Barracks);
		
		this.addToGameStates(GameState.SpecificUnit_Terran_Marine);
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return UnitType.Terran_Marine;
	}

	@Override
	public TypeWrapper defineRequiredType() {
		return TypeWrapper.generateFrom(UnitType.Terran_Barracks);
	}
}
