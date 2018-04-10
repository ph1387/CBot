package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import buildingOrderModule.scoringDirector.gameState.GameState;
import buildingOrderModule.simulator.TypeWrapper;
import bwapi.Game;
import bwapi.Mirror;
import bwapi.Pair;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapiMath.PointTypeException;
import bwapiMath.Polygon;
import bwapiMath.graph.BreadthAccessGenerator;
import bwapiMath.graph.IConnector;
import bwapiMath.graph.IInstanceMapper;
import bwapiMath.graph.RegionConnector;
import bwapiMath.graph.RegionInstanceMapper;
import bwta.BWTA;
import bwta.Chokepoint;
import bwta.Region;
import informationStorage.DistantRegion;
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

			// Reset all GameState instances. Needed when multiple games are run
			// after one another.
			GameState.resetAll();

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
				// Create the reversed Region access order. Reversed means that
				// any Unit in a Region has access to the information to which
				// Region it has to move to get to the Player's starting
				// location. This contains ALL Regions on the map as keys.
				HashMap<Region, Region> reversedRegionAccesOrder = generateReversedRegionAccessOrder(startRegion);
				// The access order for the Player's starting location.
				HashMap<Region, HashSet<Region>> regionAccessOrder = generateRegionAccessOrder(startRegion);
				// The reversed Region access orders for all Regions.
				HashMap<Region, HashMap<Region, Region>> reversedRegionAccessOrders = generateReversedRegionAccessOrders(
						reversedRegionAccesOrder.keySet());
				// The access orders for all Regions.
				HashMap<Region, HashMap<Region, HashSet<Region>>> regionAccessOrders = generateRegionAccessOrders(
						reversedRegionAccesOrder.keySet());
				// DistantRegion instances for each Region of the current map.
				HashMap<Region, HashSet<DistantRegion>> regionDistances = generateRegionDistances(regionAccessOrders,
						reversedRegionAccesOrder.keySet());

				informationStorage.getMapInfo().setReversedRegionAccessOrder(reversedRegionAccesOrder);
				informationStorage.getMapInfo().setRegionAccessOrder(regionAccessOrder);
				informationStorage.getMapInfo().setPrecomputedReversedRegionAccessOrders(reversedRegionAccessOrders);
				informationStorage.getMapInfo().setPrecomputedRegionAcccessOrders(regionAccessOrders);
				informationStorage.getMapInfo().setPrecomputedRegionDistances(regionDistances);
			}

			if (informationStorage.getiInitConfig().enableGenerateRegionTilePositions()) {
				HashMap<Region, HashSet<TilePosition>> regionTilePositions = generateRegionTilePositions();

				informationStorage.getMapInfo().setPrecomputedRegionTilePositions(regionTilePositions);
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
	 * Function for generating the reversed Region access orders for each Region
	 * inside the provided Set.
	 * <ul>
	 * <li>Key: Any Region of the provided Set.</li>
	 * <li>Value: The reversed Region access order for it in form of a
	 * HashMap.</li>
	 * </ul>
	 * 
	 * @see #generateReversedRegionAccessOrder(Region)
	 * @param regions
	 *            the Set of Regions which the reversed access orders are
	 *            generated for.
	 * @return a HashMap containing all reversed Region access orders for the
	 *         provided Set of Regions.
	 */
	private static HashMap<Region, HashMap<Region, Region>> generateReversedRegionAccessOrders(Set<Region> regions) {
		HashMap<Region, HashMap<Region, Region>> reversedRegionAccessOrders = new HashMap<>();

		// This is used instead of BWTA.getRegions() since that function returns
		// different references.
		for (Region region : regions) {
			reversedRegionAccessOrders.put(region, generateReversedRegionAccessOrder(region));
		}

		return reversedRegionAccessOrders;
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

		// This is used instead of BWTA.getRegions() since that function returns
		// different references.
		for (Region region : regions) {
			regionAccessOrders.put(region, generateRegionAccessOrder(region));
		}

		return regionAccessOrders;
	}

	// TODO: UML ADD
	/**
	 * Function for generating all DistantRegion instances for a Set of provided
	 * Regions. These Regions must be part of the Region access orders, which
	 * hold their specific access orders. The result is a HashMap with following
	 * content:
	 * <ul>
	 * <li>Key: A Region.</li>
	 * <li>Value: The DistantRegion instances that are based on the key
	 * Region.</li>
	 * </ul>
	 * 
	 * @param regionAccessOrders
	 *            the Region access orders for each Region of the provided Set.
	 * @param regions
	 *            the Regions for which the DistantRegion instances are being
	 *            generated.
	 * @return a HashMap containing different HashSets of DistantRegion
	 *         instances that are based on the provided Region instances.
	 */
	private static HashMap<Region, HashSet<DistantRegion>> generateRegionDistances(
			HashMap<Region, HashMap<Region, HashSet<Region>>> regionAccessOrders, Set<Region> regions) {
		HashMap<Region, HashSet<DistantRegion>> regionDistances = new HashMap<>();

		// This is used instead of BWTA.getRegions() since that function returns
		// different references.
		for (Region region : regions) {
			regionDistances.put(region, generateRegionDistances(regionAccessOrders.get(region), region));
		}

		return regionDistances;
	}

	// TODO: UML ADD
	/**
	 * Function for generating the DistantRegion instances based on a provided
	 * starting Region. All distances are centered around this instance while
	 * the order in which they are accessed is based on a provided Region access
	 * order.
	 * 
	 * @param regionAccessOrder
	 *            the order in which the different Regions are accessed,
	 *            beginning with the provided starting Region.
	 * @param startRegion
	 *            the Region which the distance calculations are centered
	 *            around. Must be a key in the provided Region access order.
	 * @return a HashSet containing all DistantRegion instances based on the
	 *         provided starting Region. Latter is also part of the HashSet with
	 *         a distance of 0.
	 */
	private static HashSet<DistantRegion> generateRegionDistances(HashMap<Region, HashSet<Region>> regionAccessOrder,
			Region startRegion) {
		HashSet<DistantRegion> regionDistances = new HashSet<>();
		Queue<DistantRegion> regionsToCheck = new LinkedList<>();
		regionsToCheck.add(new DistantRegion(0, startRegion, regionAccessOrder.get(startRegion)));

		while (!regionsToCheck.isEmpty()) {
			DistantRegion currentDistantRegion = regionsToCheck.poll();
			Position currentCenter = currentDistantRegion.getRegion().getCenter();

			for (Region region : currentDistantRegion.getAccessibleRegions()) {
				double distance = currentCenter.getDistance(region.getCenter());

				regionsToCheck.add(new DistantRegion(currentDistantRegion.getDistance() + distance, region,
						regionAccessOrder.get(region)));
			}

			regionDistances.add(currentDistantRegion);
		}

		return regionDistances;
	}

	// TODO: UML ADD
	/**
	 * Function for generating a HashMap containing the different Region of the
	 * current map as keys and their contained TilePositions as values:
	 * <li>Key: A Region.</li>
	 * <li>Value: The TilePositions that are inside the Region..</li>
	 * </ul>
	 * 
	 * @return a HashMap containing the Regions and their TilePositions.
	 */
	private static HashMap<Region, HashSet<TilePosition>> generateRegionTilePositions() {
		HashMap<Region, HashSet<TilePosition>> regionTilePositions = new HashMap<>();
		// This is used instead of BWTA.getRegions() since that function returns
		// different references.
		List<Region> regions = getConvertedRegionInstances();

		for (Region region : regions) {
			Polygon polygon = new Polygon(region.getPolygon());

			try {
				HashSet<TilePosition> coveredTilePositions = polygon.getCoveredTilePositions();
				regionTilePositions.put(region, coveredTilePositions);
			} catch (PointTypeException e) {
				e.printStackTrace();
			}
		}

		return regionTilePositions;
	}

	/**
	 * Function for converting the BWTA-Polygon map boundaries into standard
	 * Polygons that can be used for pathfinding etc.
	 * 
	 * @param informationStorage
	 *            the location the newly generated Polygons are stored in.
	 */
	private static void convertBWTAPolygons(InformationStorage informationStorage) {
		// This is used instead of BWTA.getRegions() since that function returns
		// different references.
		List<Region> regions = getConvertedRegionInstances();

		for (Region region : regions) {
			Polygon regionPolygon = new Polygon(region.getPolygon());

			regionPolygon.splitLongEdges(MAX_POLYGON_EDGE_LENGTH);
			informationStorage.getMapInfo().getMapBoundaries()
					.add(new Pair<bwta.Region, Polygon>(region, regionPolygon));
		}
	}

	// TODO: UML ADD
	/**
	 * Function for generating the "correct" BWTA Region references for all
	 * existing map Regions. The references are the ones obtained by the
	 * .getRegion() function.
	 * 
	 * @return a List containing all "correct" BWTA Region instances.
	 */
	private static List<Region> getConvertedRegionInstances() {
		List<Region> regions = new ArrayList<>();

		// Needed since the references given by the BWTA.getRegions function
		// differ from the ones obtained by the BWTA.getRegion function.
		for (Region region : BWTA.getRegions()) {
			regions.add(BWTA.getRegion(region.getCenter()));
		}

		return regions;
	}
}
