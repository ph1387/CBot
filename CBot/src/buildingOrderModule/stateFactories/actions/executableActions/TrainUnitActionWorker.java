package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.GameState;
import buildingOrderModule.simulator.TypeWrapper;
import bwapi.UnitType;
import core.Core;

// TODO: UML ADD
/**
 * TrainUnitActionWorker.java --- Action for training a race specific worker
 * Unit.
 * 
 * @author P H - 28.04.2017
 *
 */
public class TrainUnitActionWorker extends TrainUnitBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public TrainUnitActionWorker(Object target) {
		super(target);
		
		this.addToGameStates(GameState.Worker_Units);
		this.addToGameStates(GameState.Cheap_Units);
		this.addToGameStates(GameState.Expensive_Units);
		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Gas_Units);
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return Core.getInstance().getPlayer().getRace().getWorker();
	}

	// TODO: UML ADD FF
	@Override
	public TypeWrapper defineRequiredType() {
		return TypeWrapper.generateFrom(Core.getInstance().getPlayer().getRace().getCenter());
	}

}
