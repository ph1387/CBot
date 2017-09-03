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
	public static HashSet<TilePosition> generateNeededTilePositions(UnitType unitType,
			TilePosition targetTilePosition) {
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

		// If the Bot is Terran, also add the possible addon TilePositions to
		// the HashSet that are needed if an addon would be constructed.
		if (unitType.canBuildAddon()) {
			addAdditionalAddonSpace(unitType, targetTilePosition, neededTilePositions);
		}

		return neededTilePositions;
	}

	/**
	 * Function for adding additional TilePositions to the normal required space
	 * of the UnitType based on the addon that it is able to construct.
	 * 
	 * @param unitType
	 *            the UnitType that is going to be constructed and whose
	 *            possible addon is going to be considered.
	 * @param targetTilePosition
	 *            the TilePosition that is targeted for the UnitType's
	 *            construction.
	 * @param neededTilePositions
	 *            the HashSet of already needed TilePositions to which the addon
	 *            ones are going to be added.
	 */
	public static void addAdditionalAddonSpace(UnitType unitType, TilePosition targetTilePosition,
			HashSet<TilePosition> neededTilePositions) {
		// TODO: Possible Change: Directly get the addon from the type.
		// Differentiate between the different UnitTypes.
		if (unitType == UnitType.Terran_Factory) {
			// The Machine_Shop is build in the bottom right corner of the
			// Factory. Therefore the tile height is subtracted by 1 since the
			// TilePositions start in the upper left corner.
			UnitType addon = UnitType.Terran_Machine_Shop;
			TilePosition bottomRightCorner = new TilePosition(targetTilePosition.getX() + unitType.tileWidth(),
					targetTilePosition.getY() + unitType.tileHeight() - 1);

			for (int i = 0; i < addon.tileWidth(); i++) {
				for (int j = 0; j < addon.tileHeight(); j++) {
					neededTilePositions
							.add(new TilePosition(bottomRightCorner.getX() + i, bottomRightCorner.getY() - j));
				}
			}
		} else {
			// TODO: Needed Change: Add the missing addons.
		}
	}

}
