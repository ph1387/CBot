package core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import bwapi.Pair;
import bwapi.Unit;
import bwapi.UnitType;
import bwapiMath.Point;
import bwapiMath.Point.Direction;
import bwapiMath.Point.Type;
import bwapiMath.Polygon;
import bwta.BWTA;
import bwta.Chokepoint;

/**
 * PolygonContenderFactory.java --- Class for generating Polygons which define a
 * area in which no worker is allowed to construct buildings in.
 * 
 * @author P H - 10.09.2017
 *
 */
public class PolygonContenderFactory extends TilePositionFactory {

	public PolygonContenderFactory() {

	}

	// -------------------- Functions

	/**
	 * Function for generating the Default contended Polygons that no worker is
	 * allowed to build on.
	 * 
	 * @return a HashSet containing all default contended Polygons.
	 */
	public HashSet<Polygon> generateDefaultContendedPolygons() {
		List<Unit> startingMinerals = BWTA.getStartLocation(Core.getInstance().getPlayer()).getMinerals();
		List<Unit> startingGeysers = BWTA.getStartLocation(Core.getInstance().getPlayer()).getGeysers();
		HashSet<Polygon> contendedPolygons = new HashSet<>();

		// Generate the non building zone between the mineral spots as well as
		// the geysers and the starting position.
		try {
			this.contendPolygonInStartingLocation(contendedPolygons, startingMinerals, startingGeysers);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			this.contendPolygonsAtChokePoints(contendedPolygons);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// TODO: DEBUG INFO
		System.out.println("Default added contended Polygons: " + contendedPolygons.size());

		return contendedPolygons;
	}

	/**
	 * Function for generating a Polygon located at the Player's starting
	 * location that includes the space in between the center and the mineral
	 * patch as well as the geyser.
	 * 
	 * @param designatedHashSet
	 *            the HashSet that is going to be filled with all Polygons that
	 *            are contended from the beginning.
	 * @param startingMinerals
	 *            the minerals that are located at the starting location of the
	 *            Player.
	 * @param startingGeysers
	 *            the geysers that are located at the starting location of the
	 *            Player.
	 */
	private void contendPolygonInStartingLocation(HashSet<Polygon> designatedHashSet, List<Unit> startingMinerals,
			List<Unit> startingGeysers) {
		try {
			// Find the mineral spot and geyser that are closest to the center
			// Unit and get the direction from it to the resource depots.
			Unit centerUnit = getCenter(Core.getInstance().getPlayer().getUnits());
			Unit closestMienralSpot = this.getClosestUnit(startingMinerals, centerUnit);
			Unit closestGeyser = this.getClosestUnit(startingGeysers, centerUnit);
			Direction mineralsToBase = Point.getDirectionToSecondPoint(new Point(closestMienralSpot.getPosition()),
					new Point(centerUnit.getPosition()));
			Direction geysersToBase = Point.getDirectionToSecondPoint(new Point(closestGeyser.getPosition()),
					new Point(centerUnit.getPosition()));

			// Find Units that create the largest Polygon and instantiate it.
			Pair<Unit, Unit> unitsWithGreatestCone = this.findUnitsWhichGenerateLargestArea(startingMinerals,
					startingGeysers, mineralsToBase, geysersToBase);
			Polygon constructionFreeZone = this.createStartLocationContendedPolygon(centerUnit, unitsWithGreatestCone,
					mineralsToBase, geysersToBase);

			// Add the Polygon to the Set of Polygons.
			designatedHashSet.add(constructionFreeZone);

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
				p = new Pair<Unit, Unit>(this.findUnitWithHighestY(minerals), this.findUnitWithHighestX(geysers));
			} else if (geysersToBase == Direction.BOTTOM) {
				// top minerals, right geyser
				p = new Pair<Unit, Unit>(this.findUnitWithLowestY(minerals), this.findUnitWithHighestX(geysers));
			}
		} else if (mineralsToBase == Direction.TOP) {
			if (geysersToBase == Direction.LEFT) {
				// minerals right, geyser bottom
				p = new Pair<Unit, Unit>(this.findUnitWithHighestX(minerals), this.findUnitWithHighestY(geysers));
			} else if (geysersToBase == Direction.RIGHT) {
				// minerals left, geyser bottom
				p = new Pair<Unit, Unit>(this.findUnitWithLowestX(minerals), this.findUnitWithHighestY(geysers));
			}
		} else if (mineralsToBase == Direction.RIGHT) {
			if (geysersToBase == Direction.TOP) {
				// minerals bottom, geyser left
				p = new Pair<Unit, Unit>(this.findUnitWithHighestY(minerals), this.findUnitWithLowestX(geysers));
			} else if (geysersToBase == Direction.BOTTOM) {
				// minerals top, geyser left
				p = new Pair<Unit, Unit>(this.findUnitWithLowestY(minerals), this.findUnitWithLowestX(geysers));
			}
		} else if (mineralsToBase == Direction.BOTTOM) {
			if (geysersToBase == Direction.LEFT) {
				// minerals right, geyser top
				p = new Pair<Unit, Unit>(this.findUnitWithHighestX(minerals), this.findUnitWithLowestY(geysers));
			} else if (geysersToBase == Direction.RIGHT) {
				// minerals left, geyser top
				p = new Pair<Unit, Unit>(this.findUnitWithLowestX(minerals), this.findUnitWithLowestY(geysers));
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
		points.add(this.generateThirdPointForPolygon(unitsWithGreatestCone, mineralsToBase, geysersToBase));
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

	/**
	 * Function for generating {@link Polygon}s around {@link Chokepoint}s and
	 * adding them to a provided HashSet.
	 * 
	 * @param designatedHashSet
	 *            the HashSet that is going to be filled with all Polygons that
	 *            are contended from the beginning.
	 */
	private void contendPolygonsAtChokePoints(HashSet<Polygon> designatedHashSet) {
		List<Chokepoint> chokePoints = BWTA.getChokepoints();

		for (Chokepoint chokepoint : chokePoints) {
			designatedHashSet.add(ChokePointPolygonGenerator.generatePolygonAtChokePoint(chokepoint));
		}
	}

	// TODO: UML REMOVE
//	private Polygon generatePolygonAtChokePoint(Chokepoint chokePoint) {

	// ------------------------------ Getter / Setter

}
