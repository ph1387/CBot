package unitControlModule.stateFactories.updater;

import bwapi.Unit;
import unitControlModule.unitWrappers.PlayerUnit;
import unitControlModule.unitWrappers.PlayerUnitTerran_SiegeTank;

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
		if (playerUnit.getUnit().isSieged()) {
			this.changeWorldStateEffect("canMove", false);
			this.changeWorldStateEffect("isSieged", true);
		} else {
			this.changeWorldStateEffect("canMove", true);
			this.changeWorldStateEffect("isSieged", false);
		}

		// Mark it if the closest enemy Unit is in siege range.
		Unit closestEnemyUnitInConfidenceRange = playerUnit.getClosestEnemyUnitInConfidenceRange();

		if (closestEnemyUnitInConfidenceRange != null
				&& ((PlayerUnitTerran_SiegeTank) playerUnit).isInSiegeRange(closestEnemyUnitInConfidenceRange)) {
			this.changeWorldStateEffect("inSiegeRange", true);
		} else {
			this.changeWorldStateEffect("inSiegeRange", false);
		}
	}
}
