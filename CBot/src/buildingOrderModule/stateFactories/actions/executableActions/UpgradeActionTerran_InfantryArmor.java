package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.UpgradeType;

/**
 * UpgradeActionTerran_InfantryArmor.java --- Action for upgrading the
 * Terran_Infantry_Armor for the Terran Bio-Units at the Terran_Engineering_Bay.
 * 
 * @author P H - 22.09.2017
 *
 */
public class UpgradeActionTerran_InfantryArmor extends UpgradeBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public UpgradeActionTerran_InfantryArmor(Object target) {
		super(target);

		this.addToGameStates(GameState.Expensive_Units);
		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Gas_Units);
		this.addToGameStates(GameState.Bio_Units);
		this.addToGameStates(GameState.Upgrade_Focused);
		
		this.addToGameStates(GameState.UpgradeBioUnits);
		this.addToGameStates(GameState.SpecificUpgrade_Terran_Infantry_Armor);
	}

	// -------------------- Functions

	@Override
	protected UpgradeType defineType() {
		return UpgradeType.Terran_Infantry_Armor;
	}
}
