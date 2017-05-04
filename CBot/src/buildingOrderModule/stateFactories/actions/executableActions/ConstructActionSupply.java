package buildingOrderModule.stateFactories.actions.executableActions;

import bwapi.UnitType;
import core.Core;

/**
 * ConstructActionSupply.java --- Action for constructing a race specific supply
 * depot.
 * 
 * @author P H - 30.04.2017
 *
 */
public class ConstructActionSupply extends ConstructBaseAction {

	public ConstructActionSupply(Object target) {
		super(target);
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return Core.getInstance().getPlayer().getRace().getSupplyProvider();
	}
}
