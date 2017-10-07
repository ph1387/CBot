package unitControlModule.stateFactories.updater;

import unitControlModule.unitWrappers.PlayerUnit;

// TODO: UML ADD
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
		} else {
			this.changeWorldStateEffect("isCloaked", false);
		}
	}

}
