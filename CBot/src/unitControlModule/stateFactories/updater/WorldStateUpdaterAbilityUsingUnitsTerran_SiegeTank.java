package unitControlModule.stateFactories.updater;

import unitControlModule.unitWrappers.PlayerUnit;

/**
 * WorldStateUpdaterAbilityUsingUnitsTerran_SiegeTank.java --- WorldState
 * updater for Terran_SiegeTank WorldStates.
 * 
 * @author P H - 24.06.2017
 *
 */
public class WorldStateUpdaterAbilityUsingUnitsTerran_SiegeTank extends WorldStateUpdaterAbilityUsingUnits {

	public WorldStateUpdaterAbilityUsingUnitsTerran_SiegeTank(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions
	
	@Override
	protected void updateAbilitiyWorldState(PlayerUnit playerUnit) {
		// Adjust the moving and siege property accordingly.
		if(playerUnit.getUnit().isSieged()) {
			this.changeWorldStateEffect("canMove", false);
			this.changeWorldStateEffect("isSieged", true);
		} else {
			this.changeWorldStateEffect("canMove", true);
			this.changeWorldStateEffect("isSieged", false);
		}
	}
}
