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

	// TODO: UML ADD
	// Below this distance the SiegeTank_SiegeMode will / can not use the siege
	// attack.
	private static final int MIN_SIEGE_TILE_RANGE = 4;
	// TODO: UML ADD
	private static final int MAX_SIEGE_TILE_RANGE = 12;

	// TODO: UML ADD
	private double inSiegeRangeConfidenceMultiplier = 1.5;
	// TODO: UML ADD
	private double notInSiegeRangeConfidenceMultiplier = 0.5;

	public PlayerUnitTerran_SiegeTank(Unit unit, InformationStorage informationStorage) {
		super(unit, informationStorage);

		// TODO: Possible Change: Siege Mode range change
		// Siege mode -> min range = 2 * 32 (+ extra)
		this.extraConfidencePixelRangeToClosestUnits = 128;
	}

	// -------------------- Functions

	// TODO: UML ADD
	@Override
	protected double generateConfidence() {
		double generatedConfidence = super.generateConfidence();

		// Boost the confidence based on the range towards the closest enemy
		// Unit. If the Unit is too close and the tank is therefore unable to
		// attack it in siege mode, decrease the confidence drastically.
		if (this.isInSiegeRange(this.closestEnemyUnitInConfidenceRange)) {
			generatedConfidence *= this.inSiegeRangeConfidenceMultiplier;
		} else {
			generatedConfidence *= this.notInSiegeRangeConfidenceMultiplier;
		}

		return generatedConfidence;
	}
	
	@Override
	protected StateFactory createFactory() {
		return new StateFactoryTerran_SiegeTank();
	}

	// TODO: UML ADD
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

	// TODO: UML ADD
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

	// TODO: UML ADD
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

	// ------------------------------ Getter / Setter

	// TODO: UML ADD
	public static int getMinSiegeTileRange() {
		return MIN_SIEGE_TILE_RANGE;
	}

	// TODO: UML ADD
	public static int getMaxSiegeTileRange() {
		return MAX_SIEGE_TILE_RANGE;
	}

	// TODO: UML ADD
	public static int getMinSiegeRange() {
		return MIN_SIEGE_TILE_RANGE * Core.getInstance().getTileSize();
	}

	// TODO: UML ADD
	public static int getMaxSiegeRange() {
		return MAX_SIEGE_TILE_RANGE * Core.getInstance().getTileSize();
	}
}
