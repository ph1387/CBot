package bwapiMath.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.BiConsumer;

import bwapi.Pair;

/**
 * BreadthAccessGenerator.java --- Class containing functions for performing
 * (reversed) breadth searches with IInstanceMapper and IConnector instances.
 * 
 * @author P H - 08.03.2018
 *
 */
public class BreadthAccessGenerator {

	// -------------------- Functions

	/**
	 * Function for generating the order in which the T instances as vertices in
	 * combination with the edges of the provided IConnector instance must be
	 * traversed in order to get to the start instance. This function basically
	 * is a reversed breadth search as each T key instance in the result has the
	 * value of the T to which must be moved in order to reach the starting
	 * reference:
	 * <ul>
	 * <li>Key: A T instance.</li>
	 * <li>Value: The T instance to which must be moved in order to get closer
	 * to the starting T instance.</li>
	 * </ul>
	 * Multiple keys can point towards the same T value reference. The last
	 * reference (The start reference itself) does not point to any other T
	 * value (-> NullPointer).
	 *
	 * @param instanceMapper
	 *            the IInstanceMapper instance that is used in order to generate
	 *            the mapping of the T instances that are being used in the
	 *            graph.
	 * @param connector
	 *            the IConnector instance that is used to connect the vertices
	 *            added towards the graph. Must work together with the provided
	 *            instanceMapper.
	 * @param start
	 *            the starting T instance which all paths must lead to in the
	 *            end.
	 * @return a HashMap containing the T key and value instances. Each key is
	 *         mapped to the value that leads towards the provided starting T
	 *         instance.
	 * @throws Exception
	 *             an Exception is thrown when the starting T instance can not
	 *             be resolved in the provided instance mapper and therefore has
	 *             no associated index Integer.
	 */
	public static <T> HashMap<T, T> generateReversedBreadthAccessOrder(IInstanceMapper<T> instanceMapper,
			IConnector<T> connector, T start) throws Exception {
		HashMap<T, Integer> mappedEntries = instanceMapper.map();
		Integer startIndex = findStartIndex(mappedEntries, start);

		DirectedGraphList graph = new DirectedGraphList(mappedEntries.size());
		connector.addConnectionsToGraph(graph, mappedEntries);

		final int[] predecessors = BreadthFirstSearch.getPredecessors(graph, startIndex);

		return resolvePredecessors(predecessors, mappedEntries);
	}

	/**
	 * Function for generating the general breadth search access order in which
	 * all T instances can be accessed. Each instance used must be referred to
	 * in the provided IInstanceMapper. The instances are connected by a
	 * IConnector instance and then turned into a HashMap:
	 * <ul>
	 * <li>Key: A T instance.</li>
	 * <li>Value: The T instances that can be reached by the T key
	 * instance.</li>
	 * </ul>
	 * The top most key is the root of the tree, the start T instance.
	 *
	 * @param instanceMapper
	 *            the IInstanceMapper instance that is used in order to generate
	 *            the mapping of the T instances that are being used in the
	 *            graph.
	 * @param connector
	 *            the IConnector instance that is used to connect the vertices
	 *            added towards the graph. Must work together with the provided
	 *            instanceMapper.
	 * @param start
	 *            the starting T instance which no other instance can lead to.
	 * @return a HashMap containing the T key and value instances. Each key is
	 *         mapped to the value that leads towards the provided starting T
	 *         instance.
	 * @throws Exception
	 *             an Exception is thrown when the starting T instance can not
	 *             be resolved in the provided instance mapper and therefore has
	 *             no associated index Integer.
	 */
	public static <T> HashMap<T, HashSet<T>> generateBreadthAccessOrder(IInstanceMapper<T> instanceMapper,
			IConnector<T> connector, T start) throws Exception {
		HashMap<T, HashSet<T>> breadthAccessOrder = new HashMap<>();
		HashMap<T, T> reversedBreadthAccessOrder = generateReversedBreadthAccessOrder(instanceMapper, connector, start);

		// Iterate through each Node and store all other accessible Nodes in a
		// HashSet.
		for (T to : reversedBreadthAccessOrder.keySet()) {
			T from = reversedBreadthAccessOrder.get(to);

			if (from != null) {
				if (!breadthAccessOrder.containsKey(from)) {
					breadthAccessOrder.put(from, new HashSet<T>());
				}

				breadthAccessOrder.get(from).add(to);
			}
		}

		return breadthAccessOrder;
	}

	/**
	 * Function for finding the index that got assigned to the starting T
	 * instance with the IInstanceMpaper.
	 * 
	 * @param mappedEntries
	 *            the mapped instances that are being searched through.
	 * @param start
	 *            the reference that is being searched for.
	 * @return the index of the starting T instance that was assigned to it with
	 *         the IInstanceMapper.
	 * @throws NullPointerException
	 *             a NullPointerException is thrown when the starting reference
	 *             can not be resolved to a index Integer.
	 */
	private static <T> int findStartIndex(HashMap<T, Integer> mappedEntries, T start) throws NullPointerException {
		Integer startIndex = null;

		// Find the start index which is (hopefully) mapped to a entry.
		for (T entry : mappedEntries.keySet()) {
			if (entry.equals(start)) {
				startIndex = mappedEntries.get(entry);

				break;
			}
		}

		if (startIndex == null) {
			throw new NullPointerException();
		}

		return startIndex;
	}

	/**
	 * Function for resolving the predecessors of a finished breadth-search.
	 * This function performs a lookup using the predecessor integer array and
	 * the mapped indices of the entries. Since each entry has a unique index
	 * assigned to it, each predecessor can be decoded.
	 * 
	 * @param predecessors
	 *            the predecessor array returned by a breadth search containing
	 *            the indices of entries mapped in the provided HashMap.
	 * @param mappedEntries
	 *            a HashMap providing the instances that are mapped to unique
	 *            indices. These are used to resolve the predecessors into
	 *            actual references.
	 * @return a HashMap containing T instances:
	 *         <ul>
	 *         <li>Key: A T instance.</li>
	 *         <li>Value: A T instance which is the predecessor in the breadth
	 *         search tree (Moving up to the root!).</li>
	 *         </ul>
	 */
	private static <T> HashMap<T, T> resolvePredecessors(final int[] predecessors,
			final HashMap<T, Integer> mappedEntries) {
		HashMap<T, T> resolvedBreadthPredecessors = new HashMap<>();

		// Resolve the indices of the predecessors and save the references to a
		// separate variable.
		for (int i = 0; i < predecessors.length; i++) {
			final int currentIndex = i;
			final Pair<T, T> fromTToT = new Pair<>();

			// Find both references based on the index and the Integer
			// saved in the predecessor array:
			// -> i = from, array[i] = to
			// Or
			// -> i => array[i]
			mappedEntries.forEach(new BiConsumer<T, Integer>() {

				@Override
				public void accept(T t, Integer index) {
					if (index.equals(currentIndex)) {
						fromTToT.first = t;
					} else if (index.equals(predecessors[currentIndex])) {
						fromTToT.second = t;
					}
				}
			});

			// First: A T instance.
			// Second: The T instance that is the predecessor of the first T
			// instance.
			resolvedBreadthPredecessors.put(fromTToT.first, fromTToT.second);
		}

		return resolvedBreadthPredecessors;
	}
}
