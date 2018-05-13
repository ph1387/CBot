package unitControlModule.stateFactories.updater;

import bwapi.UnitType;
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

		// Needs to be changed to ensure that the Units move together.
		this.changeWorldStateEffect("needsGrouping", playerUnit.needsGrouping());

		// Some Units can be loaded into others. Remember resetting the values
		// in the else block when the Unit is not inside another one!
		if (playerUnit.getUnit().isLoaded()) {
			UnitType loadedIntoUnitType = playerUnit.getUnit().getTransport().getType();

			this.changeWorldStateEffect("isLoaded", true);

			if (loadedIntoUnitType == UnitType.Terran_Bunker) {
				this.changeWorldStateEffect("isLoadedIntoBunker", true);
			}
		} else {
			this.changeWorldStateEffect("isLoaded", false);
			this.changeWorldStateEffect("isLoadedIntoBunker", false);
		}
	}

}
