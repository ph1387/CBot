package unitControlModule.stateFactories.updater;

import bwapi.Unit;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * WorldStateUpdaterAbilityUsingUnitsTerran_Marine.java --- WorldState updater
 * for Terran_Marine WorldStates.
 * 
 * @author P H - 24.06.2017
 *
 */
public class WorldStateUpdaterAbilityUsingUnitsTerran_Marine extends WorldStateUpdaterAbilityUsingUnits {

	private static final int EXTRA_GROUND_WEAPON_RANGE = 32;

	public WorldStateUpdaterAbilityUsingUnitsTerran_Marine(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	protected void updateAbilitiyWorldState(PlayerUnit playerUnit) {
		Unit closestEnemy = playerUnit.getClosestEnemyUnitInConfidenceRange();

		// Only enable the StimPack if the Unit is in range of the enemies
		// ground weapon.
		if (closestEnemy != null && playerUnit.isNearPosition(closestEnemy.getTargetPosition(),
				closestEnemy.getType().groundWeapon().maxRange() + EXTRA_GROUND_WEAPON_RANGE)) {
			this.changeWorldStateEffect("canUseStimPack", true);
		} else {
			this.changeWorldStateEffect("canUseStimPack", false);
		}
	}
}
