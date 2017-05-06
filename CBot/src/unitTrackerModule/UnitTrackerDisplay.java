package unitTrackerModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

import bwapi.Color;
import bwapi.Game;
import bwapi.Position;
import bwapi.TilePosition;
import core.Core;
import core.Display;

/**
 * UnitTrackerDisplay.java --- Used for displaying various things regarding the
 * tracking of units.
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
					(lastTilePosition.getX() + enemyBuilding.getUnitType().tileWidth())
							* Core.getInstance().getTileSize(),
					(lastTilePosition.getY() + enemyBuilding.getUnitType().tileHeight())
							* Core.getInstance().getTileSize());

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
			GAME.drawTextMap(enemyUnit.getLastSeenTilePosition().toPosition().getX(),
					enemyUnit.getLastSeenTilePosition().toPosition().getY() + Core.getInstance().getLineheight(),
					enemyUnit.getUnitType().toString());
		}
	}

	/**
	 * Displays the tileStrength of the enemy units.
	 *
	 * @param valueTiles
	 *            the table of all ValueTilePositions the enemy units apply to.
	 */
	protected static void showEnemyUnitTileStrength(HashMap<TilePosition, Integer> valueTiles) {
		final Integer highestValue = findHighestValueOFTilePosition(valueTiles);

		if (highestValue != null) {
			valueTiles.forEach(new BiConsumer<TilePosition, Integer>() {

				@Override
				public void accept(TilePosition t, Integer i) {
					// Linear interpolate the color of the border
					Color displayColor = new Color((int) ((Double.valueOf(i) / Double.valueOf(highestValue)) * 255), 0,
							0);

					showUnitTileStrength(t, i, displayColor);
				}
			});
		}
	}

	/**
	 * Displays the tileStrength of the player units.
	 *
	 * @param valueTiles
	 *            the table of all ValueTilePositions the player units apply to.
	 */
	protected static void showPlayerUnitTileStrength(HashMap<TilePosition, Integer> valueTiles) {
		final Integer highestValue = findHighestValueOFTilePosition(valueTiles);

		if (highestValue != null) {
			valueTiles.forEach(new BiConsumer<TilePosition, Integer>() {

				@Override
				public void accept(TilePosition t, Integer i) {
					// Linear interpolate the color of the border
					Color displayColor = new Color(0, (int) ((Double.valueOf(i) / Double.valueOf(highestValue)) * 255),
							0);

					showUnitTileStrength(t, i, displayColor);
				}
			});
		}
	}

	/**
	 * Displays a single ValueTilePositions tileValue.
	 * 
	 * @param tilePosition
	 *            the TilePosition whose strength is being shown.
	 * @param value
	 *            the value of the TilePosition.
	 * @param displayColor
	 *            the color which the position is going to be marked with.
	 */
	private static void showUnitTileStrength(TilePosition tilePosition, Integer value, Color displayColor) {
		Display.drawTile(tilePosition.getX(), tilePosition.getY(), 1, 1, displayColor);
		GAME.drawTextMap(tilePosition.toPosition(), String.valueOf(value));
	}

	/**
	 * Function used to find the highest value in a given table of TilePositions
	 * mapped to Integers. Value needed for a linear interpolation of the color.
	 *
	 * @param valueTable
	 *            List of all ValueTilePositions taken in consideration.
	 * @return the highest Integer value of the table or null if none is found.
	 */
	private static Integer findHighestValueOFTilePosition(HashMap<TilePosition, Integer> valueTable) {
		final List<Integer> valueList = new ArrayList<Integer>();
		Integer highestValue = null;

		// Extract all values from the HashMap. Necessary since comparing them
		// directly needs the comparator to be final, which causes errors.
		valueTable.forEach(new BiConsumer<TilePosition, Integer>() {

			@Override
			public void accept(TilePosition t, Integer i) {
				valueList.add(i);
			}
		});

		for (Integer value : valueList) {
			if (highestValue == null || value > highestValue) {
				highestValue = value;
			}
		}
		return highestValue;
	}
}
