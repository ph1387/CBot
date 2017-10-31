package unitControlModule.unitWrappers;

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

	// TODO: UML CHANGE 4
	// Below this distance the SiegeTank_SiegeMode will / can not use the siege
	// attack.
	private static final int MIN_SIEGE_TILE_RANGE = 6;
	private static final int MAX_SIEGE_TILE_RANGE = 12;

	private double inSiegeRangeConfidenceMultiplier = 1.5;
	private double notInSiegeRangeConfidenceMultiplier = 0.5;

	// TODO: UML ADD
	// Flag indicating if the Unit is currently expecting another enemy one to
	// advance towards it / it's Position.
	protected boolean isExpectingEnemy = false;

	public PlayerUnitTerran_SiegeTank(Unit unit, InformationStorage informationStorage) {
		super(unit, informationStorage);
	}

	// -------------------- Functions

	// TODO: UML ADD
	@Override
	public void update() {
		super.update();

		this.updateExpectingEnemy();
	}

	// TODO: WIP IMPROVE FUNCTIONALITY
	// TODO: UML ADD
	/**
	 * Function for updating the flag indicating if the Unit is currently
	 * expecting another enemy one to advance to it / it's Position. <br>
	 * <b>Note:</b><br>
	 * Do <b>NOT</b> reset the Actions here since this can cause the Unit to
	 * permanently change from Tank_Mode to Siege_Mode. This is due to the
	 * instance being destroyed when morphing from one state to another and
	 * therefore instantiating new objects of this Class and the Siege_Mode one.
	 */
	protected void updateExpectingEnemy() {
		// Do NOT reset the Unit's Actions here! This must be done by each
		// Action itself since doing it here causes the Unit to constantly swap
		// states (Tank_Mode, Siege_Mode) due to the initial state of
		// "expectingEnemy" being false and therefore always causing a possible
		// reset.
		this.isExpectingEnemy = true;
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

	// TODO: UML ADD
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

	// TODO: UML ADD
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

	// TODO: UML ADD
	public boolean isExpectingEnemy() {
		return isExpectingEnemy;
	}

}
