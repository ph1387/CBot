package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import buildingOrderModule.simulator.TypeWrapper;
import bwapi.UnitType;
import core.Core;

/**
 * TrainUnitActionWorker.java --- Action for training a race specific worker
 * Unit.
 * 
 * @author P H - 28.04.2017
 *
 */
public class TrainUnitActionWorker extends TrainUnitBaseAction {

	private double occurrenceMultiplier = 2.;

	/**
	 * @param target
	 *            type: Integer
	 */
	public TrainUnitActionWorker(Object target) {
		super(target);

		this.addToGameStates(GameState.Worker_Units);
		this.addToGameStates(GameState.Cheap_Units);
		this.addToGameStates(GameState.Mineral_Units);

		this.addToGameStates(GameState.FreeTrainingFacility_Center);
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return Core.getInstance().getPlayer().getRace().getWorker();
	}

	@Override
	public TypeWrapper defineRequiredType() {
		return TypeWrapper.generateFrom(Core.getInstance().getPlayer().getRace().getCenter());
	}

	@Override
	public int defineMaxSimulationOccurrences() {
		return (int) (this.occurrenceMultiplier * super.defineMaxSimulationOccurrences());
	}

}
