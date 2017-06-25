package unitControlModule.stateFactories.updater;

import unitControlModule.unitWrappers.PlayerUnit;

/**
 * WorldStateUpdaterAbilityUsingUnitsTerran_Marine.java --- WorldState updater
 * for Terran_Marine WorldStates.
 * 
 * @author P H - 24.06.2017
 *
 */
public class WorldStateUpdaterAbilityUsingUnitsTerran_Marine extends WorldStateUpdaterAbilityUsingUnits {

	public WorldStateUpdaterAbilityUsingUnitsTerran_Marine(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	protected void updateAbilitiyWorldState(PlayerUnit playerUnit) {
		if(playerUnit.getUnit().isStimmed()) {
			this.changeWorldStateEffect("isStimmed", true);
		} else {
			this.changeWorldStateEffect("isStimmed", false);
		}
	}
}
