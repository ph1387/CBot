package unitControlModule.stateFactories.updater;

import unitControlModule.stateFactories.worldStates.UnitWorldStateDefault;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * SimpleWorldStateUpdater.java --- Updater for updating a
 * {@link UnitWorldStateDefault} instance.
 * 
 * @author P H - 26.02.2017
 *
 */
public class WorldStateUpdaterDefault extends WorldStateUpdaterGeneral {

	public WorldStateUpdaterDefault(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	public void update(PlayerUnit playerUnit) {
		this.changeWorldStateEffect("unitsInRange", !this.playerUnit.getAllEnemyUnitsInWeaponRange().isEmpty());
		this.changeWorldStateEffect("enemyKnown", this.playerUnit.currentState == PlayerUnit.UnitStates.ENEMY_KNOWN);

		// Never set true, since the general intent of the unit is to destroy
		// the enemy.
		this.changeWorldStateEffect("destroyUnit", false);

		// Change the move world state accordingly.
		this.changeWorldStateEffect("canMove", playerUnit.getUnit().canMove());
	}

	// TODO: UML REMOVE
	// /**
	// * Convenience function.
	// *
	// * @see WorldStateUpdaterDefault#changeWorldStateEffect(String, Object)
	// */
	// protected void changeEnemyKnown(Object value) {
	// this.changeWorldStateEffect("enemyKnown", value);
	// }
	//
	// /**
	// * Convenience function.
	// *
	// * @see WorldStateUpdaterDefault#changeWorldStateEffect(String, Object)
	// */
	// protected void changeDestroyUnit(Object value) {
	// this.changeWorldStateEffect("destroyUnit", value);
	// }
	//
	// /**
	// * Convenience function.
	// *
	// * @see WorldStateUpdaterDefault#changeWorldStateEffect(String, Object)
	// */
	// protected void changeUnitsInRange(Object value) {
	// this.changeWorldStateEffect("unitsInRange", value);
	// }

}
