package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.simulator.TypeWrapper;
import bwapi.UnitType;

// TODO: UML ADD
/**
 * TrainUnitActionTerran_SiegeTank.java --- Action for training a
 * Terran_SiegeTank.
 * 
 * @author P H - 30.04.2017
 *
 */
public class TrainUnitActionTerran_SiegeTank extends TrainUnitBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public TrainUnitActionTerran_SiegeTank(Object target) {
		super(target);
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return UnitType.Terran_Siege_Tank_Tank_Mode;
	}

	// TODO: UML ADD FF
	@Override
	public TypeWrapper defineRequiredType() {
		return TypeWrapper.generateFrom(UnitType.Terran_Machine_Shop);
	}
	
}
