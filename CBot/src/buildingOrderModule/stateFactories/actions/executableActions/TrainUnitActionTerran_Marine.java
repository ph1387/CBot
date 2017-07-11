package buildingOrderModule.stateFactories.actions.executableActions;

import bwapi.UnitType;

// TODO: UML ADD
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

	// TODO: UML ADD FF
	@Override
	public UnitType defineRequiredUnitType() {
		return UnitType.Terran_Barracks;
	}
}
