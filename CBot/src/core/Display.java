package core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiConsumer;

import bwapi.Color;
import bwapi.Game;
import bwapi.Pair;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwapiMath.Line;
import bwapiMath.Point;
import bwapiMath.Polygon;
import bwapiMath.Vector;
import bwta.Chokepoint;
import bwta.Region;
import informationStorage.InformationStorage;

/**
 * Display.java --- A class used for displaying all sorts of basic information
 * on the map and on the screen.
 * 
 * @author P H - 18.03.2017
 *
 */
public class Display {

	private static final int LINE_HEIGHT = Core.getInstance().getLineheight();
	private static final int OFFSET_LEFT = Core.getInstance().getOffsetLeft();
	private static final int TILE_SIZE = Core.getInstance().getTileSize();
	private static final Game GAME = Core.getInstance().getGame();

	// Map information visualization.
	private static final Color MAP_BONDARIES_COLOR = new Color(255, 255, 0);
	private static final Color RESERVED_SPACE_COLOR = new Color(255, 128, 0);
	private static final int POLYGON_VERTEX_RADIUS = 5;
	private static final Color CONTENDED_TILEPOSITION_COLOR = new Color(128, 128, 0);
	// TODO: UML ADD
	private static final int BLOCKING_MINERAL_POINT_RADIUS = 16;
	// TODO: UML ADD
	private static final Color BLOCKING_MINERAL_COLOR = new Color(255, 0, 0);

	/**
	 * Function for displaying a single Unit / draw a box around the tile the
	 * Unit is currently on.
	 * 
	 * @param unit
	 *            the Unit on whose current TilePosition a box is being drawn.
	 * @param color
	 *            the Color that will be used for drawing the box.
	 */
	public static void showUnitTile(Unit unit, Color color) {
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

	/**
	 * Draw a Box around one or more tiles on the map.
	 * 
	 * @param posX
	 *            the X Position the box will start at (Upper left corner).
	 * @param posY
	 *            the Y Position the box will start at (Upper left corner).
	 * @param tileWidth
	 *            the width of the box in tiles.
	 * @param tileHeight
	 *            the height of the box in tiles.
	 * @param color
	 *            the Color that the box will be displayed in.
	 */
	public static void drawTile(int posX, int posY, int tileWidth, int tileHeight, Color color) {
		Core.getInstance().getGame().drawBoxMap(posX * TILE_SIZE, posY * TILE_SIZE, (posX + tileWidth) * TILE_SIZE,
				(posY + tileHeight) * TILE_SIZE, color);
	}

	/**
	 * Draw a <b>filled</b> Box around one or more tiles on the map.
	 * 
	 * @param posX
	 *            the X Position the box will start at (Upper left corner).
	 * @param posY
	 *            the Y Position the box will start at (Upper left corner).
	 * @param tileWidth
	 *            the width of the box in tiles.
	 * @param tileHeight
	 *            the height of the box in tiles.
	 * @param color
	 *            the Color that the box will be displayed in.
	 */
	public static void drawTileFilled(int posX, int posY, int tileWidth, int tileHeight, Color color) {
		Core.getInstance().getGame().drawBoxMap(posX * TILE_SIZE, posY * TILE_SIZE, (posX + tileWidth) * TILE_SIZE,
				(posY + tileHeight) * TILE_SIZE, color, true);
	}

	/**
	 * Function for displaying the target Position of a Unit with a line towards
	 * it.
	 * 
	 * @param unit
	 *            the Unit whose target Position is being displayed.
	 * @param color
	 *            the Color of the line towards the Unit's target Position.
	 */
	public static void showUnitTarget(Unit unit, Color color) {
		GAME.drawLineMap(unit.getPosition(), unit.getTargetPosition(), color);
	}

	/**
	 * Function for displaying a given List of Units on the screen. This
	 * function displays the name of the UnitType as well as the number of times
	 * that UnitType was found inside the List.
	 * 
	 * @param units
	 *            the List of Units that is going to be displayed.
	 */
	public static void showUnits(List<Unit> units) {
		List<String> outputList = new ArrayList<String>();
		List<UnitType> alreadyCountedTypes = new ArrayList<UnitType>();

		// Count each unit and display the number on the left side of it.
		for (Unit unit : units) {
			// Do not count the buildings and avoid adding the same type more
			// than one time.
			if (!unit.getType().isBuilding() && !alreadyCountedTypes.contains(unit.getType())) {
				int unitCounter = 0;

				// Iterate through all units and
				for (Unit referenceUnit : units) {
					if (referenceUnit.getType() == unit.getType()) {
						unitCounter++;
					}
				}

				// Specify the output for a symmetric list.
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

		showList(outputList, OFFSET_LEFT, LINE_HEIGHT * 5);
	}

	/**
	 * Function for displaying a List of Strings on the screen.
	 * 
	 * @param list
	 *            the List that is going to be displayed.
	 * @param offsetX
	 *            the offset to the left.
	 * @param offsetY
	 *            the offset to the right.
	 */
	private static void showList(List<String> list, int offsetX, int offsetY) {
		for (int i = 1; i <= list.size(); i++) {
			GAME.drawTextScreen(offsetX, offsetY + (LINE_HEIGHT * i), list.get(i - 1));
		}
	}

	/**
	 * Wrapper function for displaying internal game information like the
	 * elapsed time, APM or FPS.
	 * 
	 * @param informationStorage
	 */
	public static void showGameInformation(InformationStorage informationStorage) {
		showTime(OFFSET_LEFT, LINE_HEIGHT);
		showAPM(OFFSET_LEFT, LINE_HEIGHT * 2);
		showFPS(OFFSET_LEFT, LINE_HEIGHT * 3);

		if (informationStorage.getiDisplayConfig().enableDisplayMapContendedTilePositions()) {
			showContendedTilePositions();
		}
		if (informationStorage.getiDisplayConfig().enableDisplayMapBoundaries()) {
			showBoundaries();
		}
		if (informationStorage.getiDisplayConfig().enableDisplayReservedSpacePolygons()) {
			showReservedSpacePolygons();
		}
		if (informationStorage.getiDisplayConfig().enableDisplayMineralBlockedChokePoints()) {
			showMineralBlockedChokePoints();
		}
		if (informationStorage.getiDisplayConfig().enableDisplayBreadthAccessOrder()) {
			showBreadthAccessOrder();
		}
	}

	/**
	 * Function for displaying the elapsed time. This includes the time in
	 * minutes, seconds and frames.
	 * 
	 * @param offsetX
	 *            the offset to the left.
	 * @param offsetY
	 *            the offset to the right.
	 */
	private static void showTime(int offsetX, int offsetY) {
		String minutesString = (int) Math.floor(GAME.elapsedTime() / 60) + "";
		String secondsString = "";
		int seconds = (int) GAME.elapsedTime() % 60;

		// Leading 0 regarding seconds
		if (seconds < 10) {
			secondsString = "0" + seconds;
		} else {
			secondsString = "" + seconds;
		}

		String text = "Elapsed Time: " + minutesString + ":" + secondsString + " - " + GAME.elapsedTime() + " | "
				+ GAME.getFrameCount();

		GAME.drawTextScreen(offsetX, offsetY, text);
	}

	/**
	 * Function for displaying the current APM counter.
	 * 
	 * @param offsetX
	 *            the offset to the left.
	 * @param offsetY
	 *            the offset to the right.
	 */
	private static void showAPM(int offsetX, int offsetY) {
		GAME.drawTextScreen(offsetX, offsetY, "APM: " + GAME.getAPM());
	}

	/**
	 * Function for displaying the current FPS counter.
	 * 
	 * @param offsetX
	 *            the offset to the left.
	 * @param offsetY
	 *            the offset to the right.
	 */
	private static void showFPS(int offsetX, int offsetY) {
		GAME.drawTextScreen(offsetX, offsetY, "FPS: " + GAME.getFPS());
	}

	/**
	 * Function for displaying the map's boundaries on the screen.
	 */
	private static void showBoundaries() {

		// TODO: REMOVE DEBUG WIP
		int boundaryCount = CBot.getInstance().getInformationStorage().getMapInfo().getMapBoundaries().size();
		int stepSize = 0xFFFFFF / boundaryCount;
		int currentCount = 1;
		for (Pair<Region, Polygon> pair : CBot.getInstance().getInformationStorage().getMapInfo().getMapBoundaries()) {
			int currentR = 0b111111110000000000000000 & (stepSize * currentCount);
			int currentG = 0b000000001111111100000000 & (stepSize * currentCount);
			int currentB = 0b000000000000000011111111 & (stepSize * currentCount);
			currentR = currentR >> 16;
			currentG = currentG >> 8;

			pair.second.display(new Color(currentR, currentG, currentB), true, POLYGON_VERTEX_RADIUS, false);
			currentCount++;
		}

		// Map boundaries:
		// for (Pair<Region, Polygon> pair :
		// CBot.getInstance().getInformationStorage().getMapInfo().getMapBoundaries())
		// {
		// drawPolygon(pair.second, MAP_BONDARIES_COLOR, polygonVertexRadius);
		// }
	}

	/**
	 * Function for displaying all Polygons that represent the reserved space on
	 * the map. No buildings can be constructed on TilePositions in this
	 * Polygon.
	 */
	private static void showReservedSpacePolygons() {
		for (Polygon polygon : CBot.getInstance().getInformationStorage().getMapInfo().getReservedSpace()) {
			polygon.display(RESERVED_SPACE_COLOR, true, POLYGON_VERTEX_RADIUS, false);
		}
	}

	// TODO: UML ADD
	/**
	 * Function for displaying all mineral blocked ChokePoints as well as the
	 * mineral patches that are blocking them. These ChokePoints can not be
	 * traversed by default.
	 */
	private static void showMineralBlockedChokePoints() {
		for (Pair<Unit, Chokepoint> blockedChokePoint : CBot.getInstance().getInformationStorage().getMapInfo()
				.getMineralBlockedChokePoints()) {
			// Display blocking mineral.
			(new Point(blockedChokePoint.first.getInitialPosition())).display(BLOCKING_MINERAL_POINT_RADIUS,
					BLOCKING_MINERAL_COLOR, true);

			// Display blocked ChokePoint.
			Pair<Position, Position> sides = blockedChokePoint.second.getSides();
			(new Line(new Point(sides.first), new Point(sides.second))).display(BLOCKING_MINERAL_COLOR);
		}
	}

	// TODO: UML ADD
	/**
	 * Function for displaying the breadth access order in which the different
	 * Regions can be accessed by the Bot. The Points connected by the Vectors
	 * are the centers of the Regions.
	 */
	private static void showBreadthAccessOrder() {
		CBot.getInstance().getInformationStorage().getMapInfo().getBreadthAccessOrder()
				.forEach(new BiConsumer<Region, HashSet<Region>>() {

					@Override
					public void accept(Region region, HashSet<Region> accessibleRegions) {
						for (Region accessibleRegion : accessibleRegions) {
							(new Vector(region.getCenter(), accessibleRegion.getCenter())).display();
						}
					}
				});
	}

	/**
	 * Function for displaying the contended / blocked TilePositions on the map.
	 * These are the ones that a worker is currently trying to construct a
	 * building on and / or is prohibited to build onto.
	 */
	private static void showContendedTilePositions() {
		for (TilePosition tilePosition : CBot.getInstance().getInformationStorage().getMapInfo()
				.getTilePositionContenders()) {
			drawTileFilled(tilePosition.getX(), tilePosition.getY(), 1, 1, CONTENDED_TILEPOSITION_COLOR);
		}
	}

}
