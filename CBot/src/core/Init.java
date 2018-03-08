package core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.BiConsumer;

import buildingOrderModule.simulator.TypeWrapper;
import bwapi.Game;
import bwapi.Mirror;
import bwapi.Pair;
import bwapi.Position;
import bwapi.Unit;
import bwapiMath.Polygon;
import bwapiMath.graph.BreadthFirstSearch;
import bwapiMath.graph.DirectedGraphList;
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

				// Generate the breadth access order based on the reversed one.
				informationStorage.getMapInfo().setBreadthAccessOrder(generateBreadthAccessOrder(informationStorage));
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
		HashMap<Region, Integer> regionMappedToIndex = generateRegionIndexHashMap();
		DirectedGraphList directedGraph = new DirectedGraphList(regionMappedToIndex.size());

		// TODO: DEBUG INFO
		System.out.println(regionMappedToIndex.size() + " Regions added to the connection graph.");

		// Add missing edges based on the map's ChokePoints.
		addRegionConnectionsToGraph(directedGraph, regionMappedToIndex);

		// Generate the breadth Region order starting from the Player's starting
		// one.
		return generateBreadthOrderToBase(directedGraph, regionMappedToIndex);
	}

	/**
	 * Function used for mapping each Region of the currently played map to an
	 * index / Integer. This HashMap functions as a key for the graph that is
	 * being created since the graph uses Integers and not objects as its
	 * vertices representation. Therefore a way of "encoding" and "decoding" the
	 * information is ncecessary.
	 * 
	 * @return a HashMap with each Region of the map mapped to an Integer.
	 */
	private static HashMap<Region, Integer> generateRegionIndexHashMap() {
		HashMap<Region, Integer> usedHashMap = new HashMap<>();
		int counter = 0;

		// Add an index / Integer to each Region. This is necessary since the
		// graph used is based on an adjacency List using Integers.
		for (Chokepoint chokePoint : BWTA.getChokepoints()) {
			Region regionOne = chokePoint.getRegions().first;
			Region regionTwo = chokePoint.getRegions().second;

			// Add the Region references if they are not already inside the
			// HashMap.
			if (!usedHashMap.containsKey(regionOne)) {
				usedHashMap.put(regionOne, counter);
				counter++;
			}
			if (!usedHashMap.containsKey(regionTwo)) {
				usedHashMap.put(regionTwo, counter);
				counter++;
			}
		}

		return usedHashMap;
	}

	/**
	 * Function for adding edges to a provided graph using the provided indices
	 * of the mapped Regions based on the ChokePoints of the map's Regions. This
	 * is necessary since the graph that is being used is using an adjacency
	 * List which in return uses Integers as an internal representation of its
	 * edges.
	 * 
	 * @param graph
	 *            the graph to which the edges are being added.
	 * @param regionsMappedToIndices
	 *            a HashMap which mapped the Regions of the map to Integers that
	 *            can be used to represent vertices and edges in the graph.
	 */
	private static void addRegionConnectionsToGraph(final DirectedGraphList graph,
			final HashMap<Region, Integer> regionsMappedToIndices) {
		// Add ChokePoints as edges to the created graph using the stored
		// indices in the HashMap.
		regionsMappedToIndices.forEach(new BiConsumer<Region, Integer>() {

			@Override
			public void accept(Region region, Integer index) {
				for (Chokepoint chokePoint : region.getChokepoints()) {
					boolean chokePointFree = true;

					for (Pair<Unit, Chokepoint> blockedInstance : CBot.getInstance().getInformationStorage()
							.getMapInfo().getMineralBlockedChokePoints()) {
						// Equals of the ChokePoints can NOT be used here since
						// the references are NOT the same! Therefore the
						// Position of the sides must be checked.
						if (blockedInstance.second.getSides().equals(chokePoint.getSides())) {
							chokePointFree = false;

							break;
						}
					}

					// Only add non-blocked ChokePoints to the connections.
					if (chokePointFree) {
						// Find the other region of the ChokePoint to determine
						// the
						// edge that must be added to the graph.
						Region otherRegion = chokePoint.getRegions().first;

						if (otherRegion == region) {
							otherRegion = chokePoint.getRegions().second;
						}

						graph.addEdge(index, regionsMappedToIndices.get(otherRegion));
					}
				}
			}
		});
	}

	/**
	 * Function for generating the order in which the Regions of the currently
	 * played maps must be traversed in order to get to the Player's starting
	 * location.
	 * 
	 * @param graph
	 *            the graph which will be used to determine the order of Regions
	 *            being traversed.
	 * @param regionMappedToIndex
	 *            the HashMap containing Integers mapped to Regions for decoding
	 *            latter.
	 * @return a HashMap of Regions mapped to other Regions. The value
	 *         represents the Region which must be traveled in order to get
	 *         closer to the Player's starting location.
	 */
	private static HashMap<Region, Region> generateBreadthOrderToBase(DirectedGraphList graph,
			HashMap<Region, Integer> regionMappedToIndex) {
		int start = regionMappedToIndex
				.get(BWTA.getRegion(BWTA.getStartLocation(Core.getInstance().getPlayer()).getTilePosition()));
		final int[] predecessors = BreadthFirstSearch.getPredecessors(graph, start);
		HashMap<Region, Region> breadthOrderRegions = new HashMap<>();

		// Resolve the indices of the predecessors and save the references to a
		// separate variable.
		for (int i = 0; i < predecessors.length; i++) {
			final int currentIndex = i;
			final Pair<Region, Region> regionFromTo = new Pair<>();

			// Find both Region references based on the index and the Integer
			// saved in the predecessor array.
			// -> i = from, array[i] = to
			regionMappedToIndex.forEach(new BiConsumer<Region, Integer>() {

				@Override
				public void accept(Region region, Integer index) {
					if (index.equals(currentIndex)) {
						regionFromTo.first = region;
					} else if (index.equals(predecessors[currentIndex])) {
						regionFromTo.second = region;
					}
				}
			});

			breadthOrderRegions.put(regionFromTo.first, regionFromTo.second);
		}
		return breadthOrderRegions;
	}

	/**
	 * Function for generating the order in which the different Regions are
	 * accessible beginning at the Player's starting location. This function
	 * basically returns a HashMap representing the breadth search equivalent
	 * and uses the reversed access order of the different Regions of the map.
	 * 
	 * @param informationStorage
	 *            the storages which provides the reversed access order of the
	 *            Regions.
	 * @return a HashMap containing the breadth search equivalent of the access
	 *         order of the different map Regions.
	 */
	private static HashMap<Region, HashSet<Region>> generateBreadthAccessOrder(InformationStorage informationStorage) {
		HashMap<Region, HashSet<Region>> breadthAccessOrder = new HashMap<>();

		for (Region to : informationStorage.getMapInfo().getReversedRegionAccessOrder().keySet()) {
			Region from = informationStorage.getMapInfo().getReversedRegionAccessOrder().get(to);

			if (from != null) {
				if (!breadthAccessOrder.containsKey(from)) {
					breadthAccessOrder.put(from, new HashSet<Region>());
				}

				breadthAccessOrder.get(from).add(to);
			}
		}

		return breadthAccessOrder;
	}
}
