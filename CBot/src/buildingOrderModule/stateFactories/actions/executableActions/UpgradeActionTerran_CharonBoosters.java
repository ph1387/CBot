package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.UpgradeType;

// TODO: UML ADD
/**
 * UpgradeActionTerran_CharonBoosters.java --- Action for upgrading the
 * Charon_Boosters for the Terran_Goliath at the Terran_Machine_Shop.
 * 
 * @author P H - 23.09.2017
 *
 */
public class UpgradeActionTerran_CharonBoosters extends UpgradeBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public UpgradeActionTerran_CharonBoosters(Object target) {
		super(target);

		this.addToGameStates(GameState.Expensive_Units);
		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Gas_Units);
		this.addToGameStates(GameState.Machine_Units);
		this.addToGameStates(GameState.Upgrade_Focused);
		
		this.addToGameStates(GameState.UpgradeMachineUnits);
		this.addToGameStates(GameState.SpecificUpgrade_Charon_Boosters);
	}

	// -------------------- Functions

	@Override
	protected UpgradeType defineType() {
		return UpgradeType.Charon_Boosters;
	}

}
