package unitControlModule.unitWrappers;

import java.util.LinkedList;
import java.util.Queue;

import bwapi.Pair;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import core.Core;
import informationStorage.InformationStorage;
import unitControlModule.stateFactories.StateFactory;
import unitControlModule.stateFactories.StateFactoryTerran_SiegeTank;

/**
 * PlayerUnit_Siege_Tank.java --- Terran_SiegeTank Class.
 * 
 * @author P H - 25.03.2017
 *
 */
public class PlayerUnitTerran_SiegeTank extends PlayerUnitTypeRanged {

	/**
	 * ExpectingEnemyState.java --- Enum used for switching between expecting
	 * enemy Units and not doing it at all based on a set frame time interval.
	 * 
	 * @author P H - 11.11.2017
	 *
	 */
	private enum ExpectingEnemyState {
		IDLE(-1), ACTIVE(480);

		private int activeFrames;
		private int timeStampStartFrames = -1;

		/**
		 * @param activeFrames
		 *            the number of frames that this state is active (-1 means
		 *            not active at all).
		 */
		private ExpectingEnemyState(int activeFrames) {
			this.activeFrames = activeFrames;
		}

		/**
		 * Function for testing if the set maximum number of frames exceeds the
		 * difference between the current frame count and the saved one.
		 * 
		 * @param currentFrames
		 *            the current number of frames that have passed until this
		 *            point.
		 * @return true if the set maximum number of frames exceeds the
		 *         difference between the current frame count and the saved one,
		 *         otherwise false.
		 */
		public boolean isFinished(int currentFrames) {
			return currentFrames - this.timeStampStartFrames >= this.activeFrames;
		}

		public void setTimeStampStartFrames(int timeStampStartFrames) {
			this.timeStampStartFrames = timeStampStartFrames;
		}
	}

	// Below this distance the SiegeTank_SiegeMode will / can not use the siege
	// attack.
	private static final int MIN_SIEGE_TILE_RANGE = 6;
	private static final int MAX_SIEGE_TILE_RANGE = 12;

	private double inSiegeRangeConfidenceMultiplier = 1.5;
	private double notInSiegeRangeConfidenceMultiplier = 0.5;

	private ExpectingEnemyState currentExpectingState = ExpectingEnemyState.IDLE;

	// Pair.first: TimeStamp
	// Pair.second: Combat Unit count
	private Queue<Pair<Integer, Integer>> combatUnitCounts = new LinkedList<>();
	// After more than this number of Units get destroyed in a certain frame
	// interval the Unit will be expecting enemies / swapping states.
	private int destroyedUnitsTriggerPoint = 3;
	// Combat Unit count entries older than the given number of frames are
	// discarded.
	private int maxTimeIntervalFrames = 720;

	// Flag indicating if the Unit is currently expecting another enemy one to
	// advance towards it / it's Position.
	protected boolean isExpectingEnemy = false;

	public PlayerUnitTerran_SiegeTank(Unit unit, InformationStorage informationStorage) {
		super(unit, informationStorage);

		// Make sure the Unit is not returning back to the grouping spot after
		// (re-)morphing from a sieged Siege_Tank version. This is due to the
		// instance of the PlaceUnitTerran_SiegeTank being destroyed and
		// replaced by a sieged one. Therefore a flag at a shared storage is
		// necessary. After reading the value make sure that this Unit is added
		// to the collection of marked ones!
		this.needsGrouping = !informationStorage.getSiegeTankStorage().wasAlreadyMarkedOnceAsGrouped(unit);
		informationStorage.getSiegeTankStorage().markAsGrouped(unit);
	}

	// -------------------- Functions

	@Override
	public void update() {
		super.update();

		this.updateExpectingEnemy();
	}

	/**
	 * Function for updating the flag indicating if the Unit is currently
	 * expecting another enemy one to advance to it / it's Position. <br>
	 * <b>Note:</b><br>
	 * Do <b>NOT</b> reset the Actions here since this can cause the Unit to
	 * permanently change from Tank_Mode to Siege_Mode. This is due to the
	 * instance being destroyed when morphing from one state to another and
	 * therefore instantiating new objects of this Class and the Siege_Mode one.
	 */
	private void updateExpectingEnemy() {
		int frameCount = Core.getInstance().getGame().getFrameCount();
		this.removeOutdatedCombatUnitCounts();
		this.combatUnitCounts.add(new Pair<>(frameCount,
				this.informationStorage.getCurrentGameInformation().getCurrentCombatUnitCount()));

		// Based on the number of destroyed Units in a certain frame interval
		// trigger the Unit's flag for expecting enemies.
		int numberOfDestroyedUnitsInInterval = this.generateNumberOfDestroyedUnitsInInterval();
		boolean triggerPointReached = this.destroyedUnitsTriggerPoint <= numberOfDestroyedUnitsInInterval;

		// Simple state machine for swapping between the different states the
		// Unit can be in for expecting enemies.
		if (this.currentExpectingState == ExpectingEnemyState.IDLE && triggerPointReached) {
			this.currentExpectingState = ExpectingEnemyState.ACTIVE;
			this.currentExpectingState.setTimeStampStartFrames(frameCount);
			this.isExpectingEnemy = true;
		} else if (this.currentExpectingState == ExpectingEnemyState.ACTIVE
				&& this.currentExpectingState.isFinished(frameCount)) {
			this.currentExpectingState = ExpectingEnemyState.IDLE;
			this.isExpectingEnemy = false;
		}
	}

	/**
	 * Function for removing all entries from the Queue of stored combat Unit
	 * counts that are older than a certain amount of frames.
	 */
	private void removeOutdatedCombatUnitCounts() {
		int frameCount = Core.getInstance().getGame().getFrameCount();
		boolean running = true;

		while (running) {
			Pair<Integer, Integer> currentCombatUnitCount = this.combatUnitCounts.peek();

			// Remove all entries that are older than a specified number of
			// frames.
			if (currentCombatUnitCount != null
					&& frameCount - currentCombatUnitCount.first >= this.maxTimeIntervalFrames) {
				this.combatUnitCounts.poll();
			} else {
				running = false;
			}
		}
	}

	/**
	 * Function for counting the number of destroyed Units based on the Queue of
	 * stored combat Unit counts.
	 * 
	 * @return the number of Units that got destroyed in the set interval of
	 *         frames based on the stored combat Unit count Queue.
	 */
	private int generateNumberOfDestroyedUnitsInInterval() {
		Pair<Integer, Integer> previousCombatUnitCount = null;
		int destroyedUnits = 0;

		// The difference between the previous and the current combat Unit count
		// represents the "rough" (!) number of destroyed Units. "Rough" due to
		// the Player training Units and this Class not implementing a listener
		// for "onDestroy".
		for (Pair<Integer, Integer> combatUnitCount : this.combatUnitCounts) {
			if (previousCombatUnitCount != null) {
				int difference = previousCombatUnitCount.second - combatUnitCount.second;

				// Units got destroyed.
				if (difference > 0) {
					destroyedUnits += difference;
				}
			}
			previousCombatUnitCount = combatUnitCount;
		}
		return destroyedUnits;
	}

	@Override
	protected double generateConfidence() {
		double generatedConfidence = super.generateConfidence();

		if (this.closestEnemyUnitInConfidenceRange != null) {
			// Boost the confidence based on the range towards the closest enemy
			// Unit. If the Unit is too close and the tank is therefore unable
			// to attack it in siege mode, decrease the confidence drastically.
			if (this.isInSiegeRange(this.closestEnemyUnitInConfidenceRange)) {
				generatedConfidence *= this.inSiegeRangeConfidenceMultiplier;
			} else {
				generatedConfidence *= this.notInSiegeRangeConfidenceMultiplier;
			}
		}

		return generatedConfidence;
	}

	@Override
	protected StateFactory createFactory() {
		return new StateFactoryTerran_SiegeTank();
	}

	/**
	 * Convenience function.
	 * 
	 * @param unit
	 *            the Unit that is going to be checked if it is in the siege
	 *            range.
	 * @return true if the Unit is in the siege range, false if not.
	 */
	public boolean isInSiegeRange(Unit unit) {
		return this.isInSiegeRange(unit.getPosition());
	}

	/**
	 * Convenience function.
	 * 
	 * @param tilePosition
	 *            the TilePosition that is going to be checked if it is in the
	 *            siege range.
	 * @return true if the TilePosition is in the siege range, false if not.
	 */
	public boolean isInSiegeRange(TilePosition tilePosition) {
		return this.isInSiegeRange(tilePosition.toPosition());
	}

	/**
	 * Function for checking if a Position is in the
	 * {@link PlayerUnitTerran_SiegeTank}'s siege range. This siege range has a
	 * minimum and a maximum distance that any Position must not exceed.
	 * 
	 * @param position
	 *            the Position that is going to be tested.
	 * @return true if the Position is inside the siege range, false if not.
	 */
	public boolean isInSiegeRange(Position position) {
		double distance = this.unit.getDistance(position);

		return distance > getMinSiegeRange() && distance < getMaxSiegeRange();
	}

	/**
	 * Convenience function.
	 * 
	 * @param tilePosition
	 *            the TilePosition that is going to be checked if it is in the
	 *            siege range.
	 * @return true if the Position is below the siege range, false if not.
	 */
	public boolean isBelowSiegeRange(Unit unit) {
		return this.isBelowSiegeRange(unit.getPosition());
	}

	/**
	 * Function for checking if a Position is below the
	 * {@link PlayerUnitTerran_SiegeTank}'s siege range and therefore too close
	 * to attack using the Siege_Mode.
	 * 
	 * @param position
	 *            the Position that is going to be tested.
	 * @return true if the Position is below the siege range, false if not.
	 */
	public boolean isBelowSiegeRange(Position position) {
		double distance = this.unit.getDistance(position);

		return distance < getMinSiegeRange();
	}

	// ------------------------------ Getter / Setter

	public static int getMinSiegeTileRange() {
		return MIN_SIEGE_TILE_RANGE;
	}

	public static int getMaxSiegeTileRange() {
		return MAX_SIEGE_TILE_RANGE;
	}

	public static int getMinSiegeRange() {
		return MIN_SIEGE_TILE_RANGE * Core.getInstance().getTileSize();
	}

	public static int getMaxSiegeRange() {
		return MAX_SIEGE_TILE_RANGE * Core.getInstance().getTileSize();
	}

	public boolean isExpectingEnemy() {
		return isExpectingEnemy;
	}

}
