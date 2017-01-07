package buildingModule;

import bwapi.TilePosition;
import bwapi.UnitType;

class ConstructionJob {
	
	private UnitType building;
	private TilePosition tilePosition;
	
	public ConstructionJob(UnitType building, TilePosition location) {
		this.building = building;
		this.tilePosition = location;
	}
	
	// -------------------- Functions
	
	// ------------------------------ Getter / Setter
	
	protected TilePosition getTilePosition() {
		return tilePosition;
	}
	
	protected void setTilePosition(TilePosition tilePosition) {
		this.tilePosition = tilePosition;
	}

	protected UnitType getBuilding() {
		return building;
	}
}
