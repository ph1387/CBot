package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.GameState;
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
		
		this.addToGameStates(GameState.Building_Units);
		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Cheap_Units);
		this.addToGameStates(GameState.Upgrade_Focused);
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
	
	// TODO: UML ADD
	@Override
	public int defineMaxSimulationOccurrences() {
		return 1;
	}
	
}
