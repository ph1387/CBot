package unitControlModule.stateFactories.updater;

import unitControlModule.stateFactories.worldStates.SimpleUnitWorldState;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * SimpleWorldStateUpdater.java --- Updater for updating a
 * {@link SimpleUnitWorldState} instance.
 * 
 * @author P H - 26.02.2017
 *
 */
public class SimpleWorldStateUpdater extends GeneralWorldStateUpdater {

	public SimpleWorldStateUpdater(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	public void update(PlayerUnit playerUnit) {
		if(this.playerUnit.getNearestEnemyUnitInSight() == null) {
			this.changeUnitsInSight(false);
		} else {
			this.changeUnitsInSight(true);
		}
		
		if(this.playerUnit.getAllEnemyUnitsInWeaponRange().isEmpty()) {
			this.changeUnitsInRange(false);
		} else {
			this.changeUnitsInRange(true);
		}
		
		if(this.playerUnit.currentState == PlayerUnit.UnitStates.ENEMY_MISSING) {
			this.changeEnemyKnown(false);
		} else {
			this.changeEnemyKnown(true);
		}
		
		// Never set true, since the general intent of the unit is to destroy the enemy.
		this.changeDestroyUnit(false);
	}

	/**
	 * Convenience function.
	 * 
	 * @see SimpleWorldStateUpdater#changeWorldStateEffect(String, Object)
	 */
	protected void changeEnemyKnown(Object value) {
		this.changeWorldStateEffect("enemyKnown", value);
	}

	/**
	 * Convenience function.
	 * 
	 * @see SimpleWorldStateUpdater#changeWorldStateEffect(String, Object)
	 */
	protected void changeDestroyUnit(Object value) {
		this.changeWorldStateEffect("destroyUnit", value);
	}

	/**
	 * Convenience function.
	 * 
	 * @see SimpleWorldStateUpdater#changeWorldStateEffect(String, Object)
	 */
	protected void changeUnitsInRange(Object value) {
		this.changeWorldStateEffect("unitsInRange", value);
	}

	/**
	 * Convenience function.
	 * 
	 * @see SimpleWorldStateUpdater#changeWorldStateEffect(String, Object)
	 */
	protected void changeUnitsInSight(Object value) {
		this.changeWorldStateEffect("unitsInSight", value);
	}
}
