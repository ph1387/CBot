package buildingOrderModule.stateFactories.actions.executableActions;

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

	/**
	 * @param target
	 *            type: Integer
	 */
	public TrainUnitActionWorker(Object target) {
		super(target);
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return Core.getInstance().getPlayer().getRace().getWorker();
	}
}
