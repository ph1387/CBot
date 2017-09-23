package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.simulator.TypeWrapper;
import bwapi.UnitType;

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

}