package buildingOrderModule.stateFactories.actions.executableActions;

import bwapi.UnitType;

/**
 * ConstrucActionTerran_Factory.java --- Construction action for a
 * Terran_Factory Unit.
 * 
 * @author P H - 30.04.2017
 *
 */
public class ConstrucActionTerran_Factory extends ConstructBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public ConstrucActionTerran_Factory(Object target) {
		super(target);
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return UnitType.Terran_Factory;
	}
}
