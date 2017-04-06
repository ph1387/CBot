package unitControlModule.stateFactories.actions.executableActions.worker;

import bwapi.TilePosition;
import bwapi.UnitType;

/**
 * ConstructionJob.java --- A class containing all necessary information
 * regarding the construction of one single building.
 * 
 * @author P H - 06.04.2017
 *
 */
public class ConstructionJob {

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
