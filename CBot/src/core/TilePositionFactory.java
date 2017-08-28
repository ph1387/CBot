package core;

import java.util.HashSet;

import bwapi.TilePosition;
import bwapi.UnitType;

/**
 * FactorySuperclass.java --- Superclass for worker actions depending on
 * knowledge of needed TilePositions.
 * 
 * @author P H - 28.04.2017
 *
 */
public class TilePositionFactory {

	public TilePositionFactory() {

	}

	// -------------------- Functions

	/**
	 * Function for finding all required TilePositions of a building plus a
	 * additional row at the bottom, if the building can train Units.
	 * 
	 * @param unitType
	 *            the UnitType whose TilePositions are going to be calculated.
	 * @param targetTilePosition
	 *            the TilePosition the Unit is going to be constructed /
	 *            targeted at.
	 * @return a HashSet containing all TilePositions that the constructed Unit
	 *         would have if it was constructed at the targetTilePosition.
	 */
	public static HashSet<TilePosition> generateNeededTilePositions(UnitType unitType, TilePosition targetTilePosition) {
		HashSet<TilePosition> neededTilePositions = new HashSet<TilePosition>();
		int bottomRowAddion = 0;

		if (unitType.canProduce()) {
			bottomRowAddion = 1;
		}

		for (int i = 0; i < unitType.tileWidth(); i++) {
			for (int j = 0; j < unitType.tileHeight() + bottomRowAddion; j++) {
				int targetX = targetTilePosition.getX() + i;
				int targetY = targetTilePosition.getY() + j;

				neededTilePositions.add(new TilePosition(targetX, targetY));
			}
		}
		return neededTilePositions;
	}
}
