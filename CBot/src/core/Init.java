package core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import buildingOrderModule.simulator.TypeWrapper;
import bwapi.Game;
import bwapi.Mirror;
import bwapi.Pair;
import bwapi.Position;
import bwapi.Unit;
import bwapiMath.Polygon;
import bwapiMath.graph.BreadthAccessGenerator;
import bwapiMath.graph.IConnector;
import bwapiMath.graph.IInstanceMapper;
import bwapiMath.graph.RegionConnector;
import bwapiMath.graph.RegionInstanceMapper;
import bwta.BWTA;
import bwta.Chokepoint;
import bwta.Region;
import informationStorage.InformationStorage;

/**
 * Init.java --- Class used for the initialization of the most important
 * instances.
 * 
 * @author P H - 18.03.2017
 *
 */
public class Init {

	private static final int UNIT_FLAG = 1;
	private static final int GAME_SPEED = 0; // TODO: 20, 0, etc.
	private static final int MAX_POLYGON_EDGE_LENGTH = 100;
	// TODO: UML ADD
	private static final int MINERAL_BLOCK_RANGE = 64;

	/**
	 * Function for initializing all important Functions in the beginning.
	 * 
	 * @param mirror
	 *            the mirror of the game.
	 * @return true or false depending if the action as successful or not.
	 */
	public static boolean init(Mirror mirror, InformationStorage informationStorage) {
		boolean successful = true;

		try {
			Game game = mirror.getGame();

			Core.getInstance().setMirror(mirror);

			// Initialize the TypeWrapper types for the Simulator.
			TypeWrapper.init();

			// Use BWTA to analyze map
			BWTA.readMap();
			BWTA.analyze();

			// Find all ChokePoints that are blocked by mineral patches at the
			// start of the game. This is needed since multiple components
			// depend on this information (breadth access order, etc.).
			extractMineralBlockedChokePoints(game, informationStorage);

			// Add all default contended TilePositions.
			if (informationStorage.getiInitConfig().enableGenerateDefaultContendedTilePositions()) {
				informationStorage.getMapInfo().getTilePositionContenders()
						.addAll(new TilePositionContenderFactory().generateDefaultContendedTilePositions());
			}
			if (informationStorage.getiInitConfig().enableGenerateDefaultContendedPolygons()) {
				// Add the Polygons which define a non construction area to the
				// storage.
				informationStorage.getMapInfo().getReservedSpace()
						.addAll(new PolygonContenderFactory().generateDefaultContendedPolygons());

				// Transform all created reserved Polygons into TilePositions.
				for (Polygon polygon : informationStorage.getMapInfo().getReservedSpace()) {
					informationStorage.getMapInfo().getTilePositionContenders()
							.addAll(polygon.getCoveredTilePositions());
				}
			}

			if (informationStorage.getiInitConfig().enableGenerateRegionAccessOrder()) {
				Region startRegion = BWTA
						.getRegion(BWTA.getStartLocation(Core.getInstance().getPlayer()).getTilePosition());
				HashMap<Region, HashSet<Region>> regionAccessOrder = generateRegionAccessOrder(startRegion);
				// Create the reversed Region access order. Reversed means that
				// any Unit in a Region has access to the information to which
				// Region it has to move to get to the Player's starting
				// location. This contains ALL Regions on the map as keys.
				HashMap<Region, Region> reversedRegionAccesOrder = generateReversedRegionAccessOrder(startRegion);

				informationStorage.getMapInfo().setReversedRegionAccessOrder(reversedRegionAccesOrder);
				informationStorage.getMapInfo().setRegionAccessOrder(regionAccessOrder);
				informationStorage.getMapInfo().setPrecomputedRegionAcccessOrders(
						generateRegionAccessOrders(reversedRegionAccesOrder.keySet()));
			}

			// Add all BWTA-Polygons to the collection of Polygons in the
			// InformationStorage.
			convertBWTAPolygons(informationStorage);

			// Change game settings
			game.enableFlag(UNIT_FLAG);
			game.setLocalSpeed(GAME_SPEED);
		} catch (Exception e) {
			System.out.println("---INIT FAILED---");
			e.printStackTrace();
			successful = false;
		}
		return successful;
	}

	// TODO: UML ADD
	/**
	 * Function for extracting all ChokePoints on the current map that are being
	 * blocked by minerals and therefore can not be traversed.
	 * 
	 * @param game
	 *            the game instance from which the initial mineral positions are
	 *            being taken.
	 * @param informationStorage
	 *            the location the blocked ChokePoints are being stored in.
	 */
	private static void extractMineralBlockedChokePoints(Game game, InformationStorage informationStorage) {
		HashSet<Chokepoint> alreadyAddedChokePoints = new HashSet<>();

		// Iterate through each mineral patch and add blocked ChokePoints ONCE!
		for (Unit mineral : game.getStaticMinerals()) {
			Position initialPosition = mineral.getInitialPosition();
			Chokepoint chokepoint = BWTA.getNearestChokepoint(initialPosition);

			if (chokepoint.getDistance(initialPosition) <= MINERAL_BLOCK_RANGE
					&& !alreadyAddedChokePoints.contains(chokepoint)) {
				informationStorage.getMapInfo().getMineralBlockedChokePoints()
						.add(new Pair<Unit, Chokepoint>(mineral, chokepoint));
				alreadyAddedChokePoints.add(chokepoint);
			}
		}

		// TODO: DEBUG INFO
		System.out.println(informationStorage.getMapInfo().getMineralBlockedChokePoints().size()
				+ " ChokePoints are blocked by minerals.");
	}

	// TODO: UML PARAMS
	/**
	 * Function for generating the reversed access order of all Regions of the
	 * currently played map. Each entry contains a Region as a key and another
	 * one which is the Region a Unit would have to move to to continue moving
	 * towards the provided Region. The provided one has a value of null in the
	 * HashMap since it has no previous Region. <br>
	 * <ul>
	 * <li>Key: Any Region of the currently played map.</li>
	 * <li>Value: An adjacent Region which leads towards the provided one. If
	 * the key is the provided Region, the value is null.</li>
	 * </ul>
	 * 
	 * @param startRegion
	 *            the Region that all other Regions must lead to.
	 * @return a HashMap containing Regions mapped to another adjacent one which
	 *         leads towards the provided start Region.
	 */
	private static HashMap<Region, Region> generateReversedRegionAccessOrder(Region startRegion) {
		IConnector<Region> regionConnector = new RegionConnector();
		IInstanceMapper<Region> regionInstanceMapper = new RegionInstanceMapper();
		HashMap<Region, Region> reversedAccessOrder = new HashMap<>();

		try {
			reversedAccessOrder = BreadthAccessGenerator.generateReversedBreadthAccessOrder(regionInstanceMapper,
					regionConnector, startRegion);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return reversedAccessOrder;
	}

	// TODO: UML REMOVE
	// private static HashMap<Region, Integer> generateRegionIndexHashMap() {

	// TODO: UML REMOVE
	// private static void addRegionConnectionsToGraph(final DirectedGraphList
	// graph,
	// final HashMap<Region, Integer> regionsMappedToIndices) {

	// TODO: UML REMOVE
	// private static HashMap<Region, Region>
	// generateBreadthOrderToBase(DirectedGraphList graph,
	// HashMap<Region, Integer> regionMappedToIndex) {

	// TODO: UML PARAMS
	// TODO: UML RENAME generateBreadthAccessOrder
	/**
	 * Function for generating the order in which the different Regions are
	 * accessible beginning at a provided starting Region.
	 * <ul>
	 * <li>Key: Any Region of the currently played map.</li>
	 * <li>Value: An HashSet of adjacent Regions that can be accessed by the key
	 * Region.</li>
	 * </ul>
	 * 
	 * @param startRegion
	 *            the Region the access order starts at.
	 * @return a HashMap containing the breadth search equivalent of the access
	 *         order of the different map Regions.
	 */
	private static HashMap<Region, HashSet<Region>> generateRegionAccessOrder(Region startRegion) {
		IConnector<Region> regionConnector = new RegionConnector();
		IInstanceMapper<Region> regionInstanceMapper = new RegionInstanceMapper();
		HashMap<Region, HashSet<Region>> breadthAccessOrder = new HashMap<>();

		try {
			breadthAccessOrder = BreadthAccessGenerator.generateBreadthAccessOrder(regionInstanceMapper,
					regionConnector, startRegion);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return breadthAccessOrder;
	}

	// TODO: UML ADD
	/**
	 * Function for generating the region access order for each provided Region.
	 * <ul>
	 * <li>Key: Any Region of the provided Set.</li>
	 * <li>Value: The region access order for it in form of a HashMap.</li>
	 * </ul>
	 * 
	 * @see #generateRegionAccessOrder(Region)
	 * @param regions
	 *            the Set of Regions which the access orders are generated for.
	 * @return a HashMap containing all region access orders for the provided
	 *         Set of Regions.
	 */
	private static HashMap<Region, HashMap<Region, HashSet<Region>>> generateRegionAccessOrders(Set<Region> regions) {
		HashMap<Region, HashMap<Region, HashSet<Region>>> regionAccessOrders = new HashMap<>();

		// This is used instead of BWTA.getRegions since that function returns
		// different references.
		for (Region region : regions) {
			regionAccessOrders.put(region, generateRegionAccessOrder(region));
		}

		return regionAccessOrders;
	}

	/**
	 * Function for converting the BWTA-Polygon map boundaries into standard
	 * Polygons that can be used for pathfinding etc.
	 * 
	 * @param informationStorage
	 *            the location the newly generated Polygons are stored in.
	 */
	private static void convertBWTAPolygons(InformationStorage informationStorage) {
		for (Region region : BWTA.getRegions()) {
			Polygon regionPolygon = new Polygon(region.getPolygon());

			regionPolygon.splitLongEdges(MAX_POLYGON_EDGE_LENGTH);
			informationStorage.getMapInfo().getMapBoundaries()
					.add(new Pair<bwta.Region, Polygon>(region, regionPolygon));
		}
	}

}
