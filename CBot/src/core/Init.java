package core;

import java.util.HashMap;
import java.util.HashSet;

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
				// Create the reversed Region access order. Reversed means that
				// any Unit in a Region has access to the information to which
				// Region it has to move to get to the Player's starting
				// location.
				informationStorage.getMapInfo().setReversedRegionAccessOrder(generateReversedRegionAccessOrder());
				informationStorage.getMapInfo().setRegionAccessOrder(generateRegionAccessOrder());
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

	/**
	 * Function for generating the reversed access order of all Regions of the
	 * currently played map. Each entry contains a Region as a key and another
	 * one which is the Region a Unit would have to move to to continue moving
	 * towards the Player's starting location / its home location. The Region
	 * containing the starting location is an entry as well with a value of null
	 * since no further Region leads towards the starting location. <br>
	 * <ul>
	 * <li>Key: Any Region of the currently played map.</li>
	 * <li>Value: An adjacent Region which leads towards the Player's starting
	 * location (If the key is not the Region containing the starting location).
	 * </li>
	 * </ul>
	 * 
	 * @return a HashMap containing Regions mapped to another adjacent Region
	 *         which leads towards the Player's starting location.
	 */
	private static HashMap<Region, Region> generateReversedRegionAccessOrder() {
		IConnector<Region> regionConnector = new RegionConnector();
		IInstanceMapper<Region> regionInstanceMapper = new RegionInstanceMapper();
		Region startRegion = BWTA.getRegion(BWTA.getStartLocation(Core.getInstance().getPlayer()).getTilePosition());
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

	// TODO: UML RENAME generateBreadthAccessOrder
	/**
	 * Function for generating the order in which the different Regions are
	 * accessible beginning at the Player's starting location.
	 * <ul>
	 * <li>Key: Any Region of the currently played map.</li>
	 * <li>Value: An HashSet of adjacent Regions that can be accessed by the key
	 * Region.</li>
	 * </ul>
	 * 
	 * @return a HashMap containing the breadth search equivalent of the access
	 *         order of the different map Regions.
	 */
	private static HashMap<Region, HashSet<Region>> generateRegionAccessOrder() {
		IConnector<Region> regionConnector = new RegionConnector();
		IInstanceMapper<Region> regionInstanceMapper = new RegionInstanceMapper();
		Region startRegion = BWTA.getRegion(BWTA.getStartLocation(Core.getInstance().getPlayer()).getTilePosition());
		HashMap<Region, HashSet<Region>> breadthAccessOrder = new HashMap<>();

		try {
			breadthAccessOrder = BreadthAccessGenerator.generateBreadthAccessOrder(regionInstanceMapper,
					regionConnector, startRegion);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return breadthAccessOrder;
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
