package enemyTrackerModule;

import java.util.List;

import bwapi.Color;
import bwapi.Game;
import bwapi.Position;
import bwapi.TilePosition;
import core.Core;

class EnemyTrackerDisplay {

	private static Color BUILDING_COLOR = new Color(128, 128, 128);
	private static Color UNIT_COLOR = new Color(0, 0, 255);
	private static Game GAME = Core.getInstance().getGame();
	private static int TILESIZE = 32;

	public static void showBuildingsLastPosition(List<EnemyUnit> buildingList) {
		for (EnemyUnit enemyBuilding : buildingList) {
			TilePosition lastTilePosition = enemyBuilding.getLastSeenTilePosition();
			Position endPosition = new Position(
					(lastTilePosition.getX() + enemyBuilding.getUnitType().tileWidth()) * TILESIZE,
					(lastTilePosition.getY() + enemyBuilding.getUnitType().tileHeight()) * TILESIZE);

			GAME.drawBoxMap(lastTilePosition.toPosition(), endPosition, BUILDING_COLOR);
		}
	}

	public static void showUnitsLastPosition(List<EnemyUnit> unitList) {
		for (EnemyUnit enemyUnit : unitList) {
			GAME.drawTextMap(enemyUnit.getLastSeenTilePosition().toPosition(), enemyUnit.getUnitType().toString());
		}
	}
}
