package buildingOrderModule.stateFactories.actions.executableActions;

import bwapi.UnitType;
import core.Core;

/**
 * ConstructActionRefinery.java --- Action for constructing a race specific
 * refinery.
 * 
 * @author P H - 30.04.2017
 *
 */
public class ConstructActionRefinery extends ConstructBaseAction {

	public ConstructActionRefinery(Object target) {
		super(target);
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return Core.getInstance().getPlayer().getRace().getRefinery();
	}
}
