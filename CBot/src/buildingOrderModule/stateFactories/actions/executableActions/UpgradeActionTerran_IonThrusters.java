package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.UpgradeType;

// TODO: UML ADD
/**
 * UpgradeActionTerran_IonThrusters.java --- Action for upgrading the
 * Ion_Thrusters for the Terran_Vulture at the Terran_Machine_Shop.
 * 
 * @author P H - 23.09.2017
 *
 */
public class UpgradeActionTerran_IonThrusters extends UpgradeBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public UpgradeActionTerran_IonThrusters(Object target) {
		super(target);

		this.addToGameStates(GameState.Expensive_Units);
		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Gas_Units);
		this.addToGameStates(GameState.Machine_Units);
		this.addToGameStates(GameState.Upgrade_Focused);
	}

	// -------------------- Functions

	@Override
	protected UpgradeType defineType() {
		return UpgradeType.Ion_Thrusters;
	}

}
