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

		// Mark it if the enemy Unit that the tank has to react to is in siege
		// range.
		Unit attackableEnemyUnitToReactTo = playerUnit.getAttackableEnemyUnitToReactTo();
		if (attackableEnemyUnitToReactTo != null) {
			this.changeWorldStateEffect("inSiegeRange",
					((PlayerUnitTerran_SiegeTank) playerUnit).isInSiegeRange(attackableEnemyUnitToReactTo));
			this.changeWorldStateEffect("belowSiegeRange",
					((PlayerUnitTerran_SiegeTank) playerUnit).isBelowSiegeRange(attackableEnemyUnitToReactTo));
		} else {
			this.changeWorldStateEffect("inSiegeRange", false);
			this.changeWorldStateEffect("belowSiegeRange", false);
		}

		// Change the world state regarding the expectations of enemy Units.
		this.changeWorldStateEffect("isExpectingEnemy", ((PlayerUnitTerran_SiegeTank) playerUnit).isExpectingEnemy());
	}
}
