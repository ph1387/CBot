package buildingOrderModule.stateFactories.actions.executableActions;

import bwapi.UnitType;

/**
 * TrainUnitActionTerran_SiegeTank.java --- Action for training a
 * Terran_SiegeTank.
 * 
 * @author P H - 30.04.2017
 *
 */
public class TrainUnitActionTerran_SiegeTank extends TrainUnitBaseAction {

	public TrainUnitActionTerran_SiegeTank(Object target) {
		super(target);
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return UnitType.Terran_Siege_Tank_Tank_Mode;
	}
}
