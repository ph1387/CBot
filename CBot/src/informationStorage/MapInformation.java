package informationStorage;

import java.util.HashMap;
import java.util.HashSet;

import bwapi.Pair;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapiMath.Polygon;
import bwta.Chokepoint;
import bwta.Region;

/**
 * MapInformation.java --- Class for storing different kinds of map information.
 * 
 * @author P H - 05.05.2017
 *
 */
public class MapInformation {

	private HashSet<TilePosition> tilePositionContenders = new HashSet<>();
	private HashSet<Polygon> reservedSpace = new HashSet<>();
	private HashSet<Pair<bwta.Region, Polygon>> mapBoundaries = new HashSet<>();

	// This order determines the Regions a Unit has to move along to get towards
	// the Player's starting location. A Region used as a key provides the
	// Region the Unit has to move to next (value).
	private HashMap<Region, Region> reversedRegionAccessOrder = new HashMap<>();
	// TODO: UML RENAME breadthAccessOrder
	// The "standard" Region access order based on a breadth search from the
	// Player's starting location.
	private HashMap<Region, HashSet<Region>> regionAccessOrder = new HashMap<>();
	// TOOD: UML ADD
	// A HashMap containing all region access orders for all Regions of the
	// current Map.
	// Note:
	// The key references can NOT be accessed with the BWTA.getRegions functions
	// since this one returns different references than
	// BWTA.getRegion(Position)!
	private HashMap<Region, HashMap<Region, HashSet<Region>>> precomputedRegionAcccessOrders = new HashMap<>();

	// TODO: UML ADD
	// Collection of ChokePoints that are blocked by mineral patches at the
	// beginning of the game and therefore can not be traversed by default.
	private HashSet<Pair<Unit, Chokepoint>> mineralBlockedChokePoints = new HashSet<>();

	public MapInformation() {

	}

	// -------------------- Functions

	// ------------------------------ Getter / Setter

	public HashSet<TilePosition> getTilePositionContenders() {
		return tilePositionContenders;
	}

	public HashSet<Polygon> getReservedSpace() {
		return reservedSpace;
	}

	public HashSet<Pair<bwta.Region, Polygon>> getMapBoundaries() {
		return mapBoundaries;
	}

	public HashMap<Region, Region> getReversedRegionAccessOrder() {
		return reversedRegionAccessOrder;
	}

	public void setReversedRegionAccessOrder(HashMap<Region, Region> reversedRegionAccessOrder) {
		this.reversedRegionAccessOrder = reversedRegionAccessOrder;
	}

	// TODO: UML RENAME getBreadthAccessOrder
	public HashMap<Region, HashSet<Region>> getRegionAccessOrder() {
		return regionAccessOrder;
	}

	// TODO UML RENAME setBreadthAccessOrder
	public void setRegionAccessOrder(HashMap<Region, HashSet<Region>> breadthAccessOrder) {
		this.regionAccessOrder = breadthAccessOrder;
	}

	// TODO: UML ADD
	public HashMap<Region, HashMap<Region, HashSet<Region>>> getPrecomputedRegionAcccessOrders() {
		return precomputedRegionAcccessOrders;
	}

	// TODO: UML ADD
	public void setPrecomputedRegionAcccessOrders(
			HashMap<Region, HashMap<Region, HashSet<Region>>> precomputedAcccessOrders) {
		this.precomputedRegionAcccessOrders = precomputedAcccessOrders;
	}

	// TODO: UML ADD
	public HashSet<Pair<Unit, Chokepoint>> getMineralBlockedChokePoints() {
		return mineralBlockedChokePoints;
	}
}
