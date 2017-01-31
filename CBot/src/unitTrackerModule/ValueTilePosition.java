package unitTrackerModule;

import bwapi.TilePosition;

/**
 * ValueTilePosion.java --- Wrapper for a TilePosition mapped to a strength
 * value represented by an Integer.
 * 
 * @author P H - 31.01.2017
 *
 */
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
