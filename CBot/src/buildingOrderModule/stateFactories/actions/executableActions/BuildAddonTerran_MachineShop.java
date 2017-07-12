package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.simulator.TypeWrapper;
import bwapi.UnitType;

/**
 * BuildAddonTerran_MachineShop.java --- Action for building a
 * Terran_MachineShop at a Factory.
 * 
 * @author P H - 30.04.2017
 *
 */
public class BuildAddonTerran_MachineShop extends BuildAddonBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public BuildAddonTerran_MachineShop(Object target) {
		super(target);
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return UnitType.Terran_Machine_Shop;
	}
	
	// TODO: UML ADD
	@Override
	public TypeWrapper defineRequiredType() {
		return TypeWrapper.generateFrom(UnitType.Terran_Factory);
	}
}
