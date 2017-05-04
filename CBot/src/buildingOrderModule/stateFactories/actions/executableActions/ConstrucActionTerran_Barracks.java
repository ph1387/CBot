package buildingOrderModule.stateFactories.actions.executableActions;

import bwapi.UnitType;

/**
 * ConstrucActionTerran_Barracks.java --- Construction action for a
 * Terran_Barracks Unit.
 * 
 * @author P H - 30.04.2017
 *
 */
public class ConstrucActionTerran_Barracks extends ConstructBaseAction {

	public ConstrucActionTerran_Barracks(Object target) {
		super(target);
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return UnitType.Terran_Barracks;
	}

}
