package informationStorage;

import java.util.HashSet;

import bwapi.TilePosition;
import bwapiMath.Polygon;

/**
 * MapInformation.java --- Class for storing different kinds of map information.
 * 
 * @author P H - 05.05.2017
 *
 */
public class MapInformation {

	private HashSet<TilePosition> tilePositionContenders = new HashSet<>();
	private HashSet<Polygon> reservedSpace = new HashSet<>();
	private HashSet<Polygon> mapBoundaries = new HashSet<>();
	
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
	
	public HashSet<Polygon> getMapBoundaries() {
		return mapBoundaries;
	}
}
