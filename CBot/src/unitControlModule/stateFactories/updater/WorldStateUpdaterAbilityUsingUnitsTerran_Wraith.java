package unitControlModule.stateFactories.updater;

import unitControlModule.unitWrappers.PlayerUnit;

/**
 * WorldStateUpdaterAbilityUsingUnitsTerran_Wraith.java --- WorldState updater
 * for Terran_Wraith WorldStates.
 * 
 * @author P H - 07.10.2017
 *
 */
public class WorldStateUpdaterAbilityUsingUnitsTerran_Wraith extends WorldStateUpdaterAbilityUsingUnits {

	public WorldStateUpdaterAbilityUsingUnitsTerran_Wraith(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	protected void updateAbilitiyWorldState(PlayerUnit playerUnit) {
		// Cloaking world state.
		if (playerUnit.getUnit().isCloaked()) {
			this.changeWorldStateEffect("isCloaked", true);
			this.changeWorldStateEffect("isDecloaked", false);
		} else {
			this.changeWorldStateEffect("isCloaked", false);
			this.changeWorldStateEffect("isDecloaked", true);
		}

		// Define when the Unit may use the cloaking ability.
		this.changeWorldStateEffect("mayCloak", playerUnit.getAttackableEnemyUnitToReactTo() != null);
	}

}
