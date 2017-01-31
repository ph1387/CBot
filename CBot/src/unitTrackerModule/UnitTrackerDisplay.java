package unitTrackerModule;

import java.util.List;

import bwapi.Color;
import bwapi.Game;
import bwapi.Position;
import bwapi.TilePosition;
import core.Core;
import core.Display;

/**
 * UnitTrackerDisplay.java --- Used for displaying various things regarding
 * the tracking of units.
 * 
 * @author P H - 31.01.2017
 *
 */
class UnitTrackerDisplay {

	private static Color BUILDING_COLOR = new Color(128, 128, 128);
	private static Color UNIT_COLOR = new Color(0, 0, 255);
	private static Game GAME = Core.getInstance().getGame();

	/**
	 * Displays a building on the map at its last seen position.
	 *
	 * @param buildingList
	 *            the list of buildings to be shown.
	 */
	protected static void showBuildingsLastPosition(List<EnemyUnit> buildingList) {
		for (EnemyUnit enemyBuilding : buildingList) {
			TilePosition lastTilePosition = enemyBuilding.getLastSeenTilePosition();
			Position endPosition = new Position(
					(lastTilePosition.getX() + enemyBuilding.getUnitType().tileWidth()) * Display.TILESIZE,
					(lastTilePosition.getY() + enemyBuilding.getUnitType().tileHeight()) * Display.TILESIZE);

			GAME.drawBoxMap(lastTilePosition.toPosition(), endPosition, BUILDING_COLOR);
		}
	}

	/**
	 * Displays a unit on the map at its last seen position.
	 *
	 * @param unitList
	 *            the list of units to be shown.
	 */
	protected static void showUnitsLastPosition(List<EnemyUnit> unitList) {
		for (EnemyUnit enemyUnit : unitList) {
			GAME.drawTextMap(enemyUnit.getLastSeenTilePosition().toPosition(), enemyUnit.getUnitType().toString());
		}
	}

	/**
	 * Displays the tileStrength of the enemy units.
	 *
	 * @param valueTiles
	 *            the List of all ValueTilePositions the enemy units apply to.
	 */
	protected static void showEnemyUnitTileStrength(List<ValueTilePosition> valueTiles) {
		Integer highestValue = findHighestValueOFTilePosition(valueTiles);

		if (highestValue != null) {
			for (ValueTilePosition valueTilePosition : valueTiles) {
				// Linear interpolate the color of the border
				Color displayColor = new Color(
						(int) ((Double.valueOf(valueTilePosition.getTileValue()) / Double.valueOf(highestValue)) * 255),
						0, 0);

				showUnitTileStrength(valueTilePosition.getTilePosition().getX(),
						valueTilePosition.getTilePosition().getY(), valueTilePosition, displayColor);
			}
		}
	}

	/**
	 * Displays the tileStrength of the player units.
	 *
	 * @param valueTiles
	 *            the List of all ValueTilePositions the player units apply to.
	 */
	protected static void showPlayerUnitTileStrength(List<ValueTilePosition> valueTiles) {
		Integer highestValue = findHighestValueOFTilePosition(valueTiles);

		if (highestValue != null) {
			for (ValueTilePosition valueTilePosition : valueTiles) {
				// Linear interpolate the color of the border
				Color displayColor = new Color(0,
						(int) ((Double.valueOf(valueTilePosition.getTileValue()) / Double.valueOf(highestValue)) * 255),
						0);

				showUnitTileStrength(valueTilePosition.getTilePosition().getX(),
						valueTilePosition.getTilePosition().getY(), valueTilePosition, displayColor);
			}
		}
	}

	/**
	 * Displays a single ValueTilePositions tileValue.
	 *
	 * @param tileposX
	 *            TilePosition x coordinate.
	 * @param tileposY
	 *            TilePosition y coordinate.
	 * @param valueTilePosition
	 *            the value of the TilePosition.
	 * @param displayColor
	 *            the color which the Position is going to be marked with.
	 */
	private static void showUnitTileStrength(int tileposX, int tileposY, ValueTilePosition valueTilePosition,
			Color displayColor) {
		Display.drawTile(Core.getInstance().getGame(), tileposX, tileposY, 1, 1, displayColor);
		GAME.drawTextMap(valueTilePosition.getTilePosition().toPosition(),
				String.valueOf(valueTilePosition.getTileValue()));
	}

	/**
	 * Function used to find the highest value in a given list of
	 * ValuTilePositions. Value needed for a linear interpolation of the color.
	 *
	 * @param valueList
	 *            List of all ValueTilePositions taken in consideration.
	 * @return the highest Integer value of the list or null if none is found.
	 */
	private static Integer findHighestValueOFTilePosition(List<ValueTilePosition> valueList) {
		Integer highestValue = null;

		for (ValueTilePosition valueTilePosition : valueList) {
			if (highestValue == null || valueTilePosition.getTileValue() > highestValue) {
				highestValue = valueTilePosition.getTileValue();
			}
		}
		return highestValue;
	}
}
