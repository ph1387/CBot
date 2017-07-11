package buildingOrderModule.stateFactories.actions.executableActions;

import bwapi.UnitType;
import core.Core;

// TODO: UML ADD
/**
 * ConstructActionSupply.java --- Action for constructing a race specific supply
 * depot.
 * 
 * @author P H - 30.04.2017
 *
 */
public class ConstructActionSupply extends ConstructBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public ConstructActionSupply(Object target) {
		super(target);
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return Core.getInstance().getPlayer().getRace().getSupplyProvider();
	}

}
