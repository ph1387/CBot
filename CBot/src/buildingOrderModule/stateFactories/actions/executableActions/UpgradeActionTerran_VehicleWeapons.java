package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.UpgradeType;

/**
 * UpgradeActionTerran_VehicleWeapons.java --- Action for upgrading the
 * Terran_Vehicle_Weapons for Terran machine Units at the Terran_Armory.
 * 
 * @author P H - 22.09.2017
 *
 */
public class UpgradeActionTerran_VehicleWeapons extends UpgradeBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public UpgradeActionTerran_VehicleWeapons(Object target) {
		super(target);

		this.addToGameStates(GameState.Expensive_Units);
		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Gas_Units);
		this.addToGameStates(GameState.Machine_Units);
		this.addToGameStates(GameState.Upgrade_Focused);
		
		this.addToGameStates(GameState.UpgradeMachineUnits);
		this.addToGameStates(GameState.SpecificUpgrade_Terran_Vehicle_Weapons);
	}

	// -------------------- Functions

	@Override
	protected UpgradeType defineType() {
		return UpgradeType.Terran_Vehicle_Weapons;
	}

}
