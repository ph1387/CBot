package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
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

		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Gas_Units);
		this.addToGameStates(GameState.Cheap_Units);
		this.addToGameStates(GameState.Machine_Units);
		this.addToGameStates(GameState.Upgrade_Focused);
		this.addToGameStates(GameState.Technology_Focused);

		this.addToGameStates(GameState.ResearchMachineUnits);
		this.addToGameStates(GameState.UpgradeMachineUnits);

		this.addToGameStates(GameState.SpecificBuilding_Terran_MachineShop);
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return UnitType.Terran_Machine_Shop;
	}

	@Override
	public TypeWrapper defineRequiredType() {
		return TypeWrapper.generateFrom(UnitType.Terran_Factory);
	}

	@Override
	public int defineMaxSimulationOccurrences() {
		return 1;
	}

}
