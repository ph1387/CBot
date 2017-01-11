package enemyTrackerModule;

import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

class EnemyUnit {
	
	private TilePosition lastSeenTilePosition;
	private Unit unit;
	private int timestampLastSeen = 0;
	private UnitType unitType;
	
	public EnemyUnit(TilePosition lastSeenTilePosition, Unit unit, int timestampLastSeen) {
		this.lastSeenTilePosition = lastSeenTilePosition;
		this.unit = unit;
		this.timestampLastSeen = timestampLastSeen;
		this.unitType = this.unit.getType();
	}
	
	// -------------------- Functions
	
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
