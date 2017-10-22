package unitControlModule.stateFactories.updater;

import java.util.HashSet;

import bwapi.Unit;
import bwapi.UnitType;
import unitControlModule.unitWrappers.PlayerUnit;

// TODO: UML ADD
/**
 * WorldStateUpdaterAbilityUsingUnitsTerran_Vulture.java --- WorldState updater
 * for Terran_Vulture WorldStates.
 * 
 * @author P H - 29.09.2017
 *
 */
public class WorldStateUpdaterAbilityUsingUnitsTerran_Vulture extends WorldStateUpdaterAbilityUsingUnits {

	// The distance at which enemy Units are counted towards the group.
	private int enemyGroupPixelRadius = 96;
	// The number of enemy Units that must surround the target one in order to
	// be considered a group.
	private int enemyGroupMinSize = 2;

	// The distance to other spider mines that this Unit must have in order to
	// be allowed to place another one. This is to prevent the Unit from
	// stacking all of it's mines on top of each other.
	private int minDistanceToSpiderMines = 16;

	public WorldStateUpdaterAbilityUsingUnitsTerran_Vulture(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	protected void updateAbilitiyWorldState(PlayerUnit playerUnit) {
		int enemyGroupSize = this.getEnemyGroupSize(playerUnit);

		// Change based on the spider mine count and the range towards other
		// placed spider mines.
		this.changeWorldStateEffect("canSpiderMineBePlaced",
				playerUnit.getUnit().getSpiderMineCount() > 0 && this.isNotNearSpiderMine(playerUnit));

		// Change based on the enemy Units that are near the one targeting this
		// one. If a group of them exists, enable the use of spider mines.
		this.changeWorldStateEffect("shouldSpiderMinesBePlaced", enemyGroupSize >= this.enemyGroupMinSize);

		// "isAtSpiderMineLocation" is not set due to the fact that the Unit has
		// to move to each location separately!
	}

	/**
	 * Function for calculating the group size around the target enemy Unit.
	 * 
	 * @param playerUnit
	 *            the PlayerUnit that is currently active.
	 * @return the size of the enemy Units around the target one that are
	 *         considered a group.
	 */
	private int getEnemyGroupSize(PlayerUnit playerUnit) {
		int enemyGroupSize = 0;

		if (playerUnit.getAttackingEnemyUnitToReactTo() != null) {
			// +1 since the Unit itself must be counted!
			enemyGroupSize = playerUnit.getAttackingEnemyUnitToReactTo().getUnitsInRadius(this.enemyGroupPixelRadius)
					.size() + 1;
		}
		return enemyGroupSize;
	}

	/**
	 * Function for testing if the executing Unit is <b>not</b> near a placed
	 * spider mine.
	 * 
	 * @param playerUnit
	 *            the PlayerUnit that is currently active.
	 * @return true if the Unit is not currently near a placed (Player) spider
	 *         mine.
	 */
	private boolean isNotNearSpiderMine(PlayerUnit playerUnit) {
		boolean isNearSpiderMine = false;

		for (Unit unit : playerUnit.getInformationStorage().getCurrentGameInformation().getCurrentUnits()
				.getOrDefault(UnitType.Terran_Vulture_Spider_Mine, new HashSet<Unit>())) {
			if (playerUnit.isNearPosition(unit.getPosition(), this.minDistanceToSpiderMines)) {
				isNearSpiderMine = true;
			}
		}

		return !isNearSpiderMine;
	}

}
