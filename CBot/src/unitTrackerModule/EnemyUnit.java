package unitTrackerModule;

import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

/**
 * EnemyUnit.java --- Used for storing positions mapped to enemy units and
 * timeStamps.
 * 
 * @author P H - 31.01.2017
 *
 */
public class EnemyUnit {

	private TilePosition lastSeenTilePosition;
	private Unit unit;
	private int timestampLastSeen = 0;
	private UnitType unitType;

	/**
	 * @param lastSeenTilePosition
	 *            the TilePosition the unit was last seen at.
	 * @param unit
	 *            the unit which was seen. -> If the unit is in the fog of war
	 *            values returned by this reference are most likely obfuscated /
	 *            distorted!
	 * @param timestampLastSeen
	 *            the time the unit was last seen.
	 */
	public EnemyUnit(TilePosition lastSeenTilePosition, Unit unit, int timestampLastSeen) {
		this.lastSeenTilePosition = lastSeenTilePosition;
		this.unit = unit;
		this.timestampLastSeen = timestampLastSeen;
		this.unitType = this.unit.getType();
	}

	// -------------------- Functions

	// TODO: UML ADD
	/**
	 * Function for testing if an EnemyUnit is invulnerable. This is the case if
	 * the Unit is burrowed or cloaked and no detector Unit is near it.
	 * Therefore the Player's Units are unable to attack it => Invulnerable.
	 * 
	 * @return
	 */
	public boolean isInvulnerable() {
		return (this.unit.isBurrowed() || this.unit.isCloaked()) && !this.unit.isDetected();
	}

	// -------------------- Getter / Setter

	public TilePosition getLastSeenTilePosition() {
		return lastSeenTilePosition;
	}

	public void setLastSeenTilePosition(TilePosition lastSeenTilePosition) {
		this.lastSeenTilePosition = lastSeenTilePosition;
	}

	public int getTimestampLastSeen() {
		return timestampLastSeen;
	}

	public void setTimestampLastSeen(int timestampLastSeen) {
		this.timestampLastSeen = timestampLastSeen;
	}

	public Unit getUnit() {
		return unit;
	}

	public UnitType getUnitType() {
		return this.unitType;
	}
}
