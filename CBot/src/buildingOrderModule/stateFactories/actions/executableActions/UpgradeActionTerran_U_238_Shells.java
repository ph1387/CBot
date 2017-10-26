package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.UpgradeType;

/**
 * UpgradeActionTerran_U_238_Shells.java --- Action for upgrading the
 * U_238_Shells for the Terran_Marine at the Terran_Academy.
 * 
 * @author P H - 18.09.2017
 *
 */
public class UpgradeActionTerran_U_238_Shells extends UpgradeBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public UpgradeActionTerran_U_238_Shells(Object target) {
		super(target);
		
		this.addToGameStates(GameState.Expensive_Units);
		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Gas_Units);
		this.addToGameStates(GameState.Bio_Units);
		this.addToGameStates(GameState.Upgrade_Focused);
		
		this.addToGameStates(GameState.UpgradeBioUnits);
		this.addToGameStates(GameState.SpecificUpgrade_U_238_Shells);
	}

	// -------------------- Functions

	@Override
	protected UpgradeType defineType() {
		return UpgradeType.U_238_Shells;
	}

}
