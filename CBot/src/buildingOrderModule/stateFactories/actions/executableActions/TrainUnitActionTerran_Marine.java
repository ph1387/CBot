package buildingOrderModule.stateFactories.actions.executableActions;

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
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return UnitType.Terran_Marine;
	}
}
