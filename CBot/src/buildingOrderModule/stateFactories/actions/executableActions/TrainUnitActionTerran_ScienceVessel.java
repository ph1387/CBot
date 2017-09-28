package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import buildingOrderModule.simulator.TypeWrapper;
import bwapi.UnitType;
import core.CBot;

// TODO: UML ADD
/**
 * TrainUnitActionTerran_ScienceVessel.java --- Class for training a
 * Terran_Science_Vessel.
 * 
 * @author P H - 23.09.2017
 *
 */
public class TrainUnitActionTerran_ScienceVessel extends TrainUnitBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public TrainUnitActionTerran_ScienceVessel(Object target) {
		super(target);

		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Expensive_Units);
		this.addToGameStates(GameState.Gas_Units);
		this.addToGameStates(GameState.Support_Units);
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
		return UnitType.Terran_Science_Vessel;
	}

	// TODO: UML ADD
	@Override
	protected int defineMaxTrainingCount() {
		return CBot.getInstance().getInformationStorage().getCurrentGameInformation().getCurrentUnitCounts()
				.getOrDefault(UnitType.Terran_Control_Tower, 0);
	}

}