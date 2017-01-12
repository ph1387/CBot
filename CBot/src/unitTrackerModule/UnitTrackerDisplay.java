package unitTrackerModule;

import java.util.List;

import bwapi.Color;
import bwapi.Game;
import bwapi.Position;
import bwapi.TilePosition;
import core.Core;
import display.Display;

class UnitTrackerDisplay {

	private static Color BUILDING_COLOR = new Color(128, 128, 128);
	private static Color UNIT_COLOR = new Color(0, 0, 255);
	private static Game GAME = Core.getInstance().getGame();
	private static int TILESIZE = 32;

	protected static void showBuildingsLastPosition(List<EnemyUnit> buildingList) {
		for (EnemyUnit enemyBuilding : buildingList) {
			TilePosition lastTilePosition = enemyBuilding.getLastSeenTilePosition();
			Position endPosition = new Position(
					(lastTilePosition.getX() + enemyBuilding.getUnitType().tileWidth()) * TILESIZE,
					(lastTilePosition.getY() + enemyBuilding.getUnitType().tileHeight()) * TILESIZE);

			GAME.drawBoxMap(lastTilePosition.toPosition(), endPosition, BUILDING_COLOR);
		}
	}

	protected static void showUnitsLastPosition(List<EnemyUnit> unitList) {
		for (EnemyUnit enemyUnit : unitList) {
			GAME.drawTextMap(enemyUnit.getLastSeenTilePosition().toPosition(), enemyUnit.getUnitType().toString());
		}
	}

	protected static void showEnemyUnitTileStrength(List<ValueTilePosition> valueTiles) {
		Integer highestValue = findHighestValueOFTilePosition(valueTiles);

		if(highestValue != null) {
			for (ValueTilePosition valueTilePosition : valueTiles) {
				// Linear interpolate the color of the border
				Color displayColor = new Color(
						(int) ((Double.valueOf(valueTilePosition.getTileValue()) / Double.valueOf(highestValue)) * 255), 0,
						0);

				showUnitTileStrength(valueTilePosition.getTilePosition().getX(), valueTilePosition.getTilePosition().getY(),
						valueTilePosition, displayColor);
			}
		}
	}

	protected static void showPlayerUnitTileStrength(List<ValueTilePosition> valueTiles) {
		Integer highestValue = findHighestValueOFTilePosition(valueTiles);
		
		if(highestValue != null) {
			for (ValueTilePosition valueTilePosition : valueTiles) {
				// Linear interpolate the color of the border
				Color displayColor = new Color(0,
						(int) ((Double.valueOf(valueTilePosition.getTileValue()) / Double.valueOf(highestValue)) * 255), 0);

				showUnitTileStrength(valueTilePosition.getTilePosition().getX(), valueTilePosition.getTilePosition().getY(),
						valueTilePosition, displayColor);
			}
		}
	}

	private static void showUnitTileStrength(int tileposX, int tileposY, ValueTilePosition valueTilePosition,
			Color displayColor) {
		Display.drawTile(Core.getInstance().getGame(), tileposX, tileposY, 1, 1, displayColor);
		GAME.drawTextMap(valueTilePosition.getTilePosition().toPosition(),
				String.valueOf(valueTilePosition.getTileValue()));
	}

	// Function used to find the highest value in a given list of
	// valuetilepositions. Value needed for a linear interpolation of the color.
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
