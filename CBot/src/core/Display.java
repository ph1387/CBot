package core;

import java.util.ArrayList;
import java.util.List;

import bwapi.Color;
import bwapi.Game;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwapiMath.Polygon;

/**
 * Display.java --- A class used for displaying all sorts of basic information
 * on the map and on the screen.
 * 
 * @author P H - 18.03.2017
 *
 */
public class Display {
	private static int lineHeight = Core.getInstance().getLineheight();
	private static int offsetLeft = Core.getInstance().getOffsetLeft();
	private static int tileSize = Core.getInstance().getTileSize();
	
	// Map information visualization
	private static boolean enableMapPolygons = true;
	private static boolean enableMapContendedTilePositions = true;
	private static Color mapBoundariesColor = new Color(255, 255, 0);
	private static Color reservedSpaceColor = new Color(255, 128, 0);
	private static int polygonVertexRadius = 5;
	private static Color contendedTilePositionColor = new Color(128, 128, 0);

	// Displays the unit tile ingame
	public static void showUnitTile(Game game, Unit unit, Color color) {
		int posX = unit.getTilePosition().getX();
		int posY = unit.getTilePosition().getY();
		int tileSizeX = 1;
		int tileSizeY = 1;

		// Show building sizes accordingly
		if (unit.getType().isBuilding()) {
			tileSizeX = unit.getType().tileWidth();
			tileSizeY = unit.getType().tileHeight();
		}

		drawTile(posX, posY, tileSizeX, tileSizeY, color);
	}

	// Display a box around (a) tile/-s
	public static void drawTile(int tileX, int tileY, int tileWidth, int tileHeight, Color color) {
		Core.getInstance().getGame().drawBoxMap(tileX * tileSize, tileY * tileSize, (tileX + tileWidth) * tileSize,
				(tileY + tileHeight) * tileSize, color);
	}

	// Display a filled tile on the map
	public static void drawTileFilled(int tileX, int tileY, int tileWidth, int tileHeight, Color color) {
		Core.getInstance().getGame().drawBoxMap(tileX * tileSize, tileY * tileSize, (tileX + tileWidth) * tileSize,
				(tileY + tileHeight) * tileSize, color, true);
	}

	// Display the target position of the unit
	public static void showUnitTarget(Game game, Unit unit, Color color) {
		game.drawLineMap(unit.getPosition(), unit.getTargetPosition(), color);
	}

	public static void showUnits(Game game, List<Unit> units) {
		List<String> outputList = new ArrayList<String>();
		List<UnitType> alreadyCountedTypes = new ArrayList<UnitType>();

		// Count each unit and display the number on the left side of it
		for (Unit unit : units) {
			// Do not count the buildings and avoid adding the same type more
			// than one time
			if (!unit.getType().isBuilding() && !alreadyCountedTypes.contains(unit.getType())) {
				int unitCounter = 0;

				// Iterate through all units and
				for (Unit referenceUnit : units) {
					if (referenceUnit.getType() == unit.getType()) {
						unitCounter++;
					}
				}

				// Speficy the output for a symmetric list
				String output = "";
				if (unitCounter < 10) {
					output += "  ";
				} else if (unitCounter < 100) {
					output += " ";
				}
				output += unitCounter + " - " + unit.getType();

				alreadyCountedTypes.add(unit.getType());
				outputList.add(output);
			}
		}

		showList(game, outputList, offsetLeft, lineHeight * 5);
	}

	// Display a list of strings
	private static void showList(Game game, List<String> list, int offsetX, int offsetY) {
		for (int i = 1; i <= list.size(); i++) {
			game.drawTextScreen(offsetX, offsetY + (lineHeight * i), list.get(i - 1));
		}
	}

	// Wrapper for showing game information
	public static void showGameInformation(Game game) {
		showTime(game, offsetLeft, lineHeight);
		showAPM(game, offsetLeft, lineHeight * 2);
		showFPS(game, offsetLeft, lineHeight * 3);
		
		if(enableMapPolygons) {
			showContendedTilePositions();
		}
		if(enableMapContendedTilePositions) {
			showPolygons();
		}
	}

	// Display the current time
	private static void showTime(Game game, int offsetX, int offsetY) {
		String minutesString = (int) Math.floor(game.elapsedTime() / 60) + "";
		String secondsString = "";
		int seconds = (int) game.elapsedTime() % 60;

		// Leading 0 regarding seconds
		if (seconds < 10) {
			secondsString = "0" + seconds;
		} else {
			secondsString = "" + seconds;
		}

		String text = "Elapsed Time: " + minutesString + ":" + secondsString + " - " + game.elapsedTime();

		game.drawTextScreen(offsetX, offsetY, text);
	}

	// Display the APM counter
	private static void showAPM(Game game, int offsetX, int offsetY) {
		String text = "APM: " + game.getAPM();
		game.drawTextScreen(offsetX, offsetY, text);
	}

	// Display FPS
	private static void showFPS(Game game, int offsetX, int offsetY) {
		String text = "FPS: " + game.getFPS();
		game.drawTextScreen(offsetX, offsetY, text);
	}
	
	private static void showPolygons() {
		// Map boundaries
		for (Polygon polygon : CBot.getInstance().getInformationStorage().getMapInfo().getMapBoundaries()) {
			polygon.drawOnMap(mapBoundariesColor, polygonVertexRadius);
		}
		
		// Custom Polygons
		for (Polygon polygon : CBot.getInstance().getInformationStorage().getMapInfo().getReservedSpace()) {
			polygon.drawOnMap(reservedSpaceColor, polygonVertexRadius);
		}
	}

	private static void showContendedTilePositions() {
		for (TilePosition tilePosition : CBot.getInstance().getInformationStorage().getMapInfo().getTilePositionContenders()) {
			drawTileFilled(tilePosition.getX(), tilePosition.getY(), 1, 1, contendedTilePositionColor);
		}
	}
}
