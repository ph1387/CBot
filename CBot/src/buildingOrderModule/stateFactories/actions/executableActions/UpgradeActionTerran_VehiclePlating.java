package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.UpgradeType;

/**
 * UpgradeActionTerran_VehiclePlating.java --- Action for upgrading the
 * Terran_Vehicle_Plating for Terran machine Units at the Terran_Armory.
 * 
 * @author P H - 22.09.2017
 *
 */
public class UpgradeActionTerran_VehiclePlating extends UpgradeBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public UpgradeActionTerran_VehiclePlating(Object target) {
		super(target);

		this.addToGameStates(GameState.Expensive_Units);
		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Gas_Units);
		this.addToGameStates(GameState.Machine_Units);
		this.addToGameStates(GameState.Upgrade_Focused);
		
		this.addToGameStates(GameState.UpgradeMachineUnits);
		this.addToGameStates(GameState.SpecificUpgrade_Terran_Vehicle_Plating);
	}

	// -------------------- Functions

	@Override
	protected UpgradeType defineType() {
		return UpgradeType.Terran_Vehicle_Plating;
	}

}
