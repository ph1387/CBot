package core;

import java.util.HashMap;
import java.util.function.BiConsumer;

import bwapi.Game;
import bwapi.Mirror;
import bwapi.Pair;
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
	private static final int GAME_SPEED = 200; // TODO: 20, 0, etc.
	private static final int MAX_POLYGON_EDGE_LENGTH = 100;

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

			// Use BWTA to analyze map
			BWTA.readMap();
			BWTA.analyze();

			// TODO: WIP: Disable on Custom Maps!
			// Add all default contended TilePositions.
//			informationStorage.getMapInfo().getTilePositionContenders()
//					.addAll(new TilePositionContenderFactory(CBot.getInstance().getInformationStorage())
//							.generateDefaultContendedTilePositions());

			// Create the reversed Region access order. Reversed means that any
			// Unit in a Region has access to the information to which Region it
			// has to move to get to the Player's starting location.
//			informationStorage.getMapInfo().setReversedRegionAccessOrder(generateReversedRegionAccessOrder());

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

		// TODO: REMOVE DEBUG
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
					// Find the other region of the ChokePoint to determine the
					// edge that must be added to the graph.
					Region otherRegion = chokePoint.getRegions().first;

					if (otherRegion == region) {
						otherRegion = chokePoint.getRegions().second;
					}

					graph.addEdge(index, regionsMappedToIndices.get(otherRegion));
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
}
