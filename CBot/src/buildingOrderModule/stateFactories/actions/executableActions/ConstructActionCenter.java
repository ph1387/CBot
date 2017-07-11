package buildingOrderModule.stateFactories.actions.executableActions;

import bwapi.UnitType;
import core.Core;

//TODO: UML ADD
/**
 * ConstructActionCenter.java --- Action for constructing a race specific center
 * building.
 * 
 * @author P H - 30.04.2017
 *
 */
public class ConstructActionCenter extends ConstructBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public ConstructActionCenter(Object target) {
		super(target);
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return Core.getInstance().getPlayer().getRace().getCenter();
	}
	
}
