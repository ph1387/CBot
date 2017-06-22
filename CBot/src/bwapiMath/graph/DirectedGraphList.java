package bwapiMath.graph;

import java.util.LinkedList;
import java.util.List;

/**
 * DirectedGraphList.java --- Graph Class used for representing a simple
 * directed graph.
 * 
 * @author P H - 14.06.2017
 *
 */
public class DirectedGraphList {

	private LinkedList<Integer>[] connections;
	private int size;

	@SuppressWarnings("unchecked")
	public DirectedGraphList(int size) {
		this.size = size;
		connections = new LinkedList[size];

		// Initialize the lists
		for (int i = 0; i < connections.length; i++) {
			connections[i] = new LinkedList<Integer>();
		}
	}

	public int size() {
		return this.size;
	}

	public boolean hasEdge(int node1, int node2) {
		return connections[node1].contains(node2);
	}

	public void addEdge(int node1, int node2) {
		connections[node1].add(node2);
	}

	public void removeEdge(int node1, int node2) {
		connections[node1].remove(node2);
	}

	public List<Integer> adjacentNodes(int node) {
		LinkedList<Integer> nodes = new LinkedList<Integer>();

		for (int i = 0; i < this.connections[node].size(); i++) {
			nodes.add(connections[node].get(i));
		}
		return nodes;
	}
}
