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
	// Collection of ChokePoints that are blocked by mineral patches at the
	// beginning of the game and therefore can not be traversed by default.
	private HashSet<Pair<Unit, Chokepoint>> mineralBlockedChokePoints = new HashSet<>();

	// ----------
	// Note:
	// The key references can NOT be accessed with the BWTA.getRegions functions
	// since this one returns different references than
	// BWTA.getRegion(Position)!
	// ----------

	private HashSet<Pair<Region, Polygon>> mapBoundaries = new HashSet<>();

	// This order determines the Regions a Unit has to move along to get towards
	// the Player's starting location. A Region used as a key provides the
	// Region the Unit has to move to next (value).
	private HashMap<Region, Region> reversedRegionAccessOrder = new HashMap<>();
	// The "standard" Region access order based on a breadth search from the
	// Player's starting location.
	private HashMap<Region, HashSet<Region>> regionAccessOrder = new HashMap<>();
	// A HashMap containing all reversed Region access orders for all Region of
	// the current map.
	private HashMap<Region, HashMap<Region, Region>> precomputedReversedRegionAccessOrders = new HashMap<>();
	// A HashMap containing all Region access orders for all Regions of the
	// current map.
	private HashMap<Region, HashMap<Region, HashSet<Region>>> precomputedRegionAcccessOrders = new HashMap<>();
	// A HashMap containing precomputed distances in order to minimize the
	// calculation time in certain algorithms. All DistantRegion instances are
	// based on the Region key they are accessed with.
	private HashMap<Region, HashSet<DistantRegion>> precomputedRegionDistances = new HashMap<>();
	// A HashMap containing the Regions and their corresponding TilePositions
	// based on the current map.
	private HashMap<Region, HashSet<TilePosition>> precomputedRegionTilePositions = new HashMap<>();

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

	public HashSet<Pair<Unit, Chokepoint>> getMineralBlockedChokePoints() {
		return mineralBlockedChokePoints;
	}

	public HashMap<Region, Region> getReversedRegionAccessOrder() {
		return reversedRegionAccessOrder;
	}

	public void setReversedRegionAccessOrder(HashMap<Region, Region> reversedRegionAccessOrder) {
		this.reversedRegionAccessOrder = reversedRegionAccessOrder;
	}

	public HashMap<Region, HashSet<Region>> getRegionAccessOrder() {
		return regionAccessOrder;
	}

	public void setRegionAccessOrder(HashMap<Region, HashSet<Region>> breadthAccessOrder) {
		this.regionAccessOrder = breadthAccessOrder;
	}

	public HashMap<Region, HashMap<Region, Region>> getPrecomputedReversedRegionAccessOrders() {
		return precomputedReversedRegionAccessOrders;
	}

	public void setPrecomputedReversedRegionAccessOrders(
			HashMap<Region, HashMap<Region, Region>> precomputedReversedRegionAccessOrders) {
		this.precomputedReversedRegionAccessOrders = precomputedReversedRegionAccessOrders;
	}

	public HashMap<Region, HashMap<Region, HashSet<Region>>> getPrecomputedRegionAcccessOrders() {
		return precomputedRegionAcccessOrders;
	}

	public void setPrecomputedRegionAcccessOrders(
			HashMap<Region, HashMap<Region, HashSet<Region>>> precomputedAcccessOrders) {
		this.precomputedRegionAcccessOrders = precomputedAcccessOrders;
	}

	public HashMap<Region, HashSet<DistantRegion>> getPrecomputedRegionDistances() {
		return precomputedRegionDistances;
	}

	public void setPrecomputedRegionDistances(HashMap<Region, HashSet<DistantRegion>> precomputedRegionDistances) {
		this.precomputedRegionDistances = precomputedRegionDistances;
	}

	public HashMap<Region, HashSet<TilePosition>> getPrecomputedRegionTilePositions() {
		return precomputedRegionTilePositions;
	}

	public void setPrecomputedRegionTilePositions(
			HashMap<Region, HashSet<TilePosition>> precomputedRegionTilePositions) {
		this.precomputedRegionTilePositions = precomputedRegionTilePositions;
	}

}
