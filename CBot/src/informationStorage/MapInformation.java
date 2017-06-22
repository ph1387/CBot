package informationStorage;

import java.util.HashMap;
import java.util.HashSet;

import bwapi.Pair;
import bwapi.TilePosition;
import bwapiMath.Polygon;
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
}
