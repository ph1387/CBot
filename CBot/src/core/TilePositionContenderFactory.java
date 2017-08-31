package core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import bwapi.Game;
import bwapi.Pair;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwapiMath.Point;
import bwapiMath.Polygon;
import bwapiMath.Point.Direction;
import bwapiMath.Point.Type;
import bwta.BWTA;
import informationStorage.InformationStorage;

/**
 * TilePositionContenderGenerator.java --- Class for generating the default
 * contended construction spots on the map, on which no worker can construct a
 * building.
 * 
 * @author P H - 21.04.2017
 *
 */
public class TilePositionContenderFactory extends TilePositionFactory {

	private int contendedTileRangeBorder = 1;
	private int contendedTileRangeMinerals = 3;
	private int contendedTileRangeGeysers = 3;

	private InformationStorage informationStorage;
	
	public TilePositionContenderFactory(InformationStorage informationStorage) {
		this.informationStorage = informationStorage;
	}
	
	// -------------------- Functions

	/**
	 * Function for generating the Default contended TilePositions in the
	 * beginning of the game. These include a x wide path at the map border, y
	 * tiles around all mineral spots and a z wide ring around all gas geysers.
	 * 
	 * @return a HashSet containing all default contended TilePositions.
	 */
	public HashSet<TilePosition> generateDefaultContendedTilePositions() {
		HashSet<TilePosition> defaultContendedTilePositions = new HashSet<TilePosition>();
		List<Unit> startingMinerals = BWTA.getStartLocation(Core.getInstance().getPlayer()).getMinerals();
		List<Unit> startingGeysers = BWTA.getStartLocation(Core.getInstance().getPlayer()).getGeysers();

		// Get all important contended default spots on the map.
		contendTilePositionsAroundMinerals(defaultContendedTilePositions, startingMinerals);
		contendTilePositionsAroundGeysers(defaultContendedTilePositions, startingGeysers);
		contendTilePositionsAtMapEdges(defaultContendedTilePositions);

		// Generate the non building zone between the mineral spots as well as
		// the geysers and the starting position.
		try {
			contendTilePositionsInStartingLocation(defaultContendedTilePositions, startingMinerals, startingGeysers);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// TODO: DEBUG INFO
		System.out.println("Total default contended TilePositions: " + defaultContendedTilePositions.size());

		return defaultContendedTilePositions;
	}

	/**
	 * Function for contending all TilePositions in a specific area around each
	 * mineral spot on the map.
	 * 
	 * @param designatedHashSet
	 *            the HashSet in which the TilePositions are going to be stored.
	 * @param excludedUnits
	 *            the Units that are not going to be looked at during the
	 *            iterations.
	 */
	public void contendTilePositionsAroundMinerals(HashSet<TilePosition> designatedHashSet,
			List<Unit> excludedUnits) {
		// TODO: DEBUG INFO
		System.out.println("Default contended TilePositions excluded mineral spots:");

		for (Unit unit : Core.getInstance().getGame().getMinerals()) {
			if (!excludedUnits.contains(unit)) {
				for (int i = unit.getTilePosition().getX() - this.contendedTileRangeMinerals; i <= unit.getTilePosition()
						.getX() + this.contendedTileRangeMinerals; i++) {
					for (int j = unit.getTilePosition().getY() - this.contendedTileRangeMinerals; j <= unit
							.getTilePosition().getY() + this.contendedTileRangeMinerals; j++) {
						TilePosition generatedTilePosition = new TilePosition(i, j);

						if (!designatedHashSet.contains(generatedTilePosition)
								&& Core.getInstance().getGame().getUnitsOnTile(generatedTilePosition).isEmpty()) {
							designatedHashSet.add(generatedTilePosition);
						}
					}
				}
			} else {
				// TODO: DEBUG INFO
				System.out.println("  - " + unit.getTilePosition() + " " + unit);
			}
		}
	}

	/**
	 * Function for contending all TilePositions in a specific area around each
	 * geyser on the map.
	 * 
	 * @param designatedHashSet
	 *            the HashSet in which the TilePositions are going to be stored.
	 * @param excludedUnits
	 *            the Units that are not going to be looked at during the
	 *            iterations.
	 */
	public void contendTilePositionsAroundGeysers(HashSet<TilePosition> designatedHashSet,
			List<Unit> excludedUnits) {
		// TODO: DEBUG INFO
		System.out.println("Default contended TilePositions excluded geysers:");

		for (Unit unit : Core.getInstance().getGame().getGeysers()) {
			if (!excludedUnits.contains(unit)) {
				for (int i = unit.getTilePosition().getX() - this.contendedTileRangeGeysers; i <= unit.getTilePosition()
						.getX() + this.contendedTileRangeGeysers; i++) {
					for (int j = unit.getTilePosition().getY() - this.contendedTileRangeGeysers; j <= unit
							.getTilePosition().getY() + this.contendedTileRangeGeysers; j++) {
						TilePosition generatedTilePosition = new TilePosition(i, j);

						if (!designatedHashSet.contains(generatedTilePosition)
								&& Core.getInstance().getGame().getUnitsOnTile(generatedTilePosition).isEmpty()) {
							designatedHashSet.add(generatedTilePosition);
						}
					}
				}
			} else {
				// TODO: DEBUG INFO
				System.out.println("  - " + unit.getTilePosition() + " " + unit);
			}
		}
	}

	/**
	 * Function for contending all TilePositions in a specific area at the map
	 * edge.
	 * 
	 * @param designatedHashSet
	 *            the HashSet in which the TilePositions are going to be stored.
	 */
	public void contendTilePositionsAtMapEdges(HashSet<TilePosition> designatedHashSet) {
		Game game = Core.getInstance().getGame();

		// Width and height are both reduced by 1 since the TilePositions
		// leading to the ends are needed.
		// -> top and bottom
		for (int i = 0; i < this.contendedTileRangeBorder; i++) {
			for (int j = 0; j < game.mapWidth(); j++) {
				TilePosition topTilePosition = new TilePosition(j, i);
				TilePosition bottomTilePosition = new TilePosition(j, game.mapHeight() - 1 - i);

				if (!designatedHashSet.contains(topTilePosition) && game.getUnitsOnTile(topTilePosition).isEmpty()) {
					designatedHashSet.add(topTilePosition);
				}
				if (!designatedHashSet.contains(bottomTilePosition)
						&& game.getUnitsOnTile(bottomTilePosition).isEmpty()) {
					designatedHashSet.add(bottomTilePosition);
				}
			}
		}
		// -> left and right
		for (int i = 0; i < this.contendedTileRangeBorder; i++) {
			for (int j = 0; j < game.mapHeight(); j++) {
				TilePosition leftTilePosition = new TilePosition(i, j);
				TilePosition rightTilePosition = new TilePosition(game.mapWidth() - 1 - i, j);

				if (!designatedHashSet.contains(leftTilePosition) && game.getUnitsOnTile(leftTilePosition).isEmpty()) {
					designatedHashSet.add(leftTilePosition);
				}
				if (!designatedHashSet.contains(rightTilePosition)
						&& game.getUnitsOnTile(rightTilePosition).isEmpty()) {
					designatedHashSet.add(rightTilePosition);
				}
			}
		}
	}

	/**
	 * Main function for filling a predefined HashSet with all TilePositions
	 * that are contended from the beginning. This includes various
	 * environmental as well as starting factors like mineral spots and geysers
	 * on the map, the map border or the space that is required for the starting
	 * workers to gather resources and more. The starting mineral spots as well
	 * as the geysers have to be separately addressed since they receive a
	 * special treatment using a Polygon.
	 * 
	 * @param designatedHashSet
	 *            the HashSet that is going to be filled with all TilePositions
	 *            that are contended from the beginning.
	 * @param startingMinerals
	 *            the minerals that are located at the starting location of the
	 *            Player.
	 * @param startingGeysers
	 *            the geysers that are located at the starting location of the
	 *            Player.
	 */
	public void contendTilePositionsInStartingLocation(HashSet<TilePosition> designatedHashSet,
			List<Unit> startingMinerals, List<Unit> startingGeysers) {
		try {
			// Find the mineral spot and geyser that are closest to the center
			// Unit and get the direction from it to the resource depots.
			Unit centerUnit = getCenter(Core.getInstance().getPlayer().getUnits());
			Unit closestMienralSpot = getClosestUnit(startingMinerals, centerUnit);
			Unit closestGeyser = getClosestUnit(startingGeysers, centerUnit);
			Direction mineralsToBase = Point.getDirectionToSecondPoint(new Point(closestMienralSpot.getPosition()),
					new Point(centerUnit.getPosition()));
			Direction geysersToBase = Point.getDirectionToSecondPoint(new Point(closestGeyser.getPosition()),
					new Point(centerUnit.getPosition()));

			// Find Units that create the largest Polygon and instantiate it.
			Pair<Unit, Unit> unitsWithGreatestCone = findUnitsWhichGenerateLargestArea(startingMinerals,
					startingGeysers, mineralsToBase, geysersToBase);
			Polygon constructionFreeZone = createStartLocationContendedPolygon(centerUnit, unitsWithGreatestCone,
					mineralsToBase, geysersToBase);

			// Add all covered TilePositions except the ones directly on top of
			// the geysers. This ensures that the construction of the first
			// refinery is not blocked and causing a deadlock.
			designatedHashSet.addAll(constructionFreeZone.getCoveredTilePositions());

			for (Unit unit : startingGeysers) {
				designatedHashSet.removeAll(
						generateNeededTilePositions(UnitType.Resource_Vespene_Geyser, unit.getTilePosition()));
			}

			// Add the Polygon to the Set of Polygons
			this.informationStorage.getMapInfo().getReservedSpace().add(constructionFreeZone);
			
			// TODO: DEBUG INFO
			System.out.println("Minerals: " + mineralsToBase + " Geysers: " + geysersToBase);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Function for getting a center Unit (CommandCenter, Nexus, ...) from a
	 * List of given Units
	 * 
	 * @param units
	 *            the List of Units in which a center Unit is going to be
	 *            searched for.
	 * @return a center Unit if one is found or null.
	 */
	private Unit getCenter(List<Unit> units) {
		UnitType centerType = Core.getInstance().getPlayer().getRace().getCenter();

		for (Unit unit : units) {
			if (unit.getType() == centerType) {
				return unit;
			}
		}
		return null;
	}

	/**
	 * Function for finding the closest Unit in an iterable collection.
	 * 
	 * @param units
	 *            the collection that is going to be searched.
	 * @param targetUnit
	 *            the reference Unit to which the closest Unit from the
	 *            collection is returned later on.
	 * @return the closest Unit to the given reference Unit.
	 */
	private Unit getClosestUnit(Iterable<Unit> units, Unit targetUnit) {
		Unit closestUnit = null;

		for (Unit unit : units) {
			if (closestUnit == null || unit.getDistance(targetUnit) < closestUnit.getDistance(targetUnit)) {
				closestUnit = unit;
			}
		}
		return closestUnit;
	}

	/**
	 * Function for finding the two Units that together produce the largest area
	 * possible. These two Units are chosen from two Lists: the minerals and the
	 * geysers. Based on the direction of each one to the center Unit, two are
	 * chosen that create a Polygon with the maximum area.
	 * 
	 * @param minerals
	 *            the List of mineral spots from which one Unit is chosen.
	 * @param geysers
	 *            the List of geysers from which one Unit is chosen.
	 * @param mineralsToBase
	 *            the direction from the center Unit towards the mineral spots.
	 * @param geysersToBase
	 *            the direction from the center Unit towards the geysers.
	 * @return a Pair of Units that together with the center Unit create a
	 *         Polygon with the largest possible area.
	 */
	private Pair<Unit, Unit> findUnitsWhichGenerateLargestArea(List<Unit> minerals, List<Unit> geysers,
			Direction mineralsToBase, Direction geysersToBase) {
		Pair<Unit, Unit> p = null;

		// The direction towards the center can not be the same since every map
		// used with bwapi is setting the geysers either left or right of the
		// minerals.
		if (mineralsToBase == Direction.LEFT) {
			if (geysersToBase == Direction.TOP) {
				// bottom minerals, right geyser
				p = new Pair<Unit, Unit>(findUnitWithHighestY(minerals), findUnitWithHighestX(geysers));
			} else if (geysersToBase == Direction.BOTTOM) {
				// top minerals, right geyser
				p = new Pair<Unit, Unit>(findUnitWithLowestY(minerals), findUnitWithHighestX(geysers));
			}
		} else if (mineralsToBase == Direction.TOP) {
			if (geysersToBase == Direction.LEFT) {
				// minerals right, geyser bottom
				p = new Pair<Unit, Unit>(findUnitWithHighestX(minerals), findUnitWithHighestY(geysers));
			} else if (geysersToBase == Direction.RIGHT) {
				// minerals left, geyser bottom
				p = new Pair<Unit, Unit>(findUnitWithLowestX(minerals), findUnitWithHighestY(geysers));
			}
		} else if (mineralsToBase == Direction.RIGHT) {
			if (geysersToBase == Direction.TOP) {
				// minerals bottom, geyser left
				p = new Pair<Unit, Unit>(findUnitWithHighestY(minerals), findUnitWithLowestX(geysers));
			} else if (geysersToBase == Direction.BOTTOM) {
				// minerals top, geyser left
				p = new Pair<Unit, Unit>(findUnitWithLowestY(minerals), findUnitWithLowestX(geysers));
			}
		} else if (mineralsToBase == Direction.BOTTOM) {
			if (geysersToBase == Direction.LEFT) {
				// minerals right, geyser top
				p = new Pair<Unit, Unit>(findUnitWithHighestX(minerals), findUnitWithLowestY(geysers));
			} else if (geysersToBase == Direction.RIGHT) {
				// minerals left, geyser top
				p = new Pair<Unit, Unit>(findUnitWithLowestX(minerals), findUnitWithLowestY(geysers));
			}
		}

		return p;
	}

	/**
	 * Function for finding the Unit with the highest x coordinate.
	 * 
	 * @param units
	 *            the List of Units that is going to be searched.
	 * @return the Unit with the highest x coordinate or null if the List is
	 *         empty.
	 */
	private Unit findUnitWithHighestX(List<Unit> units) {
		Unit currentlyBestSuitedUnit = null;

		for (Unit unit : units) {
			if (currentlyBestSuitedUnit == null
					|| currentlyBestSuitedUnit.getPosition().getX() < unit.getPosition().getX()) {
				currentlyBestSuitedUnit = unit;
			}
		}
		return currentlyBestSuitedUnit;
	}

	/**
	 * Function for finding the Unit with the lowest x coordinate.
	 * 
	 * @param units
	 *            the List of Units that is going to be searched.
	 * @return the Unit with the lowest x coordinate or null if the List is
	 *         empty.
	 */
	private Unit findUnitWithLowestX(List<Unit> units) {
		Unit currentlyBestSuitedUnit = null;

		for (Unit unit : units) {
			if (currentlyBestSuitedUnit == null
					|| currentlyBestSuitedUnit.getPosition().getX() > unit.getPosition().getX()) {
				currentlyBestSuitedUnit = unit;
			}
		}
		return currentlyBestSuitedUnit;
	}

	/**
	 * Function for finding the Unit with the highest y coordinate.
	 * 
	 * @param units
	 *            the List of Units that is going to be searched.
	 * @return the Unit with the highest y coordinate or null if the List is
	 *         empty.
	 */
	private Unit findUnitWithHighestY(List<Unit> units) {
		Unit currentlyBestSuitedUnit = null;

		for (Unit unit : units) {
			if (currentlyBestSuitedUnit == null
					|| currentlyBestSuitedUnit.getPosition().getY() < unit.getPosition().getY()) {
				currentlyBestSuitedUnit = unit;
			}
		}
		return currentlyBestSuitedUnit;
	}

	/**
	 * Function for finding the Unit with the lowest y coordinate.
	 * 
	 * @param units
	 *            the List of Units that is going to be searched.
	 * @return the Unit with the lowest y coordinate or null if the List is
	 *         empty.
	 */
	private Unit findUnitWithLowestY(List<Unit> units) {
		Unit currentlyBestSuitedUnit = null;

		for (Unit unit : units) {
			if (currentlyBestSuitedUnit == null
					|| currentlyBestSuitedUnit.getPosition().getY() > unit.getPosition().getY()) {
				currentlyBestSuitedUnit = unit;
			}
		}
		return currentlyBestSuitedUnit;
	}

	/**
	 * Function for generating a Polygon from the center Unit and two provided
	 * Units as well as a fourth one, that is generated individually.
	 *
	 * @param centerUnit
	 *            the center Unit that the Polygon is starting from.
	 * @param unitsWithGreatestCone
	 *            the Units whose Positions will function as vertices on the
	 *            Polygon.
	 * @param mineralsToBase
	 *            the direction from the center Unit towards the mineral spots.
	 * @param geysersToBase
	 *            the direction from the center Unit towards the geysers.
	 * @return a Polygon covering the largest possible area with the given
	 *         Units.
	 */
	private Polygon createStartLocationContendedPolygon(Unit centerUnit, Pair<Unit, Unit> unitsWithGreatestCone,
			Direction mineralsToBase, Direction geysersToBase) {
		List<Point> points = new ArrayList<Point>();

		points.add(new Point(centerUnit.getPosition()));
		points.add(new Point(unitsWithGreatestCone.first.getPosition()));
		points.add(generateThirdPointForPolygon(unitsWithGreatestCone, mineralsToBase, geysersToBase));
		points.add(new Point(unitsWithGreatestCone.second.getPosition()));

		return new Polygon(points);
	}

	/**
	 * Function for generating another Point for the Polygon, that is inserted
	 * at the third position in the List of vertices. This Point marks the
	 * Position at which the combined coordinates from the second (geyser or
	 * mineral) and the fourth (mineral or geyser) Unit produce the Polygon with
	 * the largest possible area.
	 * 
	 * @param mineralAndGeyser
	 *            the Pair of Units that are used to generate the initial
	 *            Polygon together with the center Unit.
	 * @param mineralsToBase
	 *            the direction from the center Unit towards the mineral spots.
	 * @param geysersToBase
	 *            the direction from the center Unit towards the geysers.
	 * @return a Point which inserted into the Polygon at the third Position
	 *         (between the mineral spot and the geyser) produces the largest
	 *         possible area in a Polygon.
	 */
	private Point generateThirdPointForPolygon(Pair<Unit, Unit> mineralAndGeyser, Direction mineralsToBase,
			Direction geysersToBase) {
		Point p = null;

		if (mineralsToBase == Direction.TOP || mineralsToBase == Direction.BOTTOM) {
			p = new Point(mineralAndGeyser.second.getPosition().getX(), mineralAndGeyser.first.getPosition().getY(),
					Type.POSITION);
		} else if (mineralsToBase == Direction.LEFT || mineralsToBase == Direction.RIGHT) {
			p = new Point(mineralAndGeyser.first.getPosition().getX(), mineralAndGeyser.second.getPosition().getY(),
					Type.POSITION);
		}
		return p;
	}
}
