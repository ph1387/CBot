package unitTrackerModule;

import bwapi.TilePosition;

class ValueTilePosition {

	private TilePosition tilePosition;
	private int tileValue = 0;
	
	public ValueTilePosition(TilePosition tilePosition) {
		this.tilePosition = tilePosition;
	}
	
	// -------------------- Functions
	
	protected void addToTileValue(int amount) {
		this.tileValue += amount;
	}
	
	protected void resetTileValue() {
		this.tileValue = 0;
	}
	
	// ------------------------------ Getter / Setter
	
	public TilePosition getTilePosition() {
		return tilePosition;
	}

	public int getTileValue() {
		return tileValue;
	}
}
