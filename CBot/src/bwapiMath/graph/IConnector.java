package bwapiMath.graph;

import java.util.HashMap;

//TODO: UML ADD
/**
 * IConnector.java --- Interface defining the function for adding edges to a
 * DirectedGraph instance.
 * 
 * @author P H - 08.03.2018
 *
 */
public interface IConnector<T> {

	/**
	 * Function for adding edges to a provided graph using the provided indices
	 * of the mapped T instances. These T instances can be anything, i.e.
	 * Regions, ChokePoints, Positions, etc.. As long as a mapped instance
	 * exists the reference can be used. This is necessary since the graph that
	 * is being used is using an adjacency List which in return uses Integers as
	 * an internal representation of its edges.
	 * 
	 * @param graph
	 *            the graph to which the edges are being added.
	 * @param mappedInstances
	 *            a HashMap which mapps the used T instances to Integers that
	 *            can be used to represent vertices and edges in the graph.
	 */
	public void addConnectionsToGraph(DirectedGraphList graph, HashMap<T, Integer> mappedInstances);
}
