package bwapiMath.graph;

import java.util.LinkedList;
import java.util.Queue;

/**
 * BreadthFirstSearch.java --- Simple Class used for performing a
 * BreadthFirstSearch on a given {@link DirectedGraphList}.
 * 
 * @author P H - 14.06.2017
 *
 */
public class BreadthFirstSearch {
	private enum c {
		WHITE, GREY, BLACK
	};

	public static int[] getPredecessors(DirectedGraphList graph, int start) {
		int size = graph.size();

		c[] color = new c[size];
		int[] dist = new int[size];
		int[] pred = new int[size];

		breadthSearch(graph, start, color, dist, pred);

		return pred;
	}

	private static void breadthSearch(DirectedGraphList graph, int start, c[] color, int[] dist, int[] pred) {
		// Add default values
		for (int i = 0; i < graph.size(); i++) {
			color[i] = c.WHITE;
			dist[i] = -1;
			pred[i] = -1;
		}

		Queue<Integer> queue = new LinkedList<>();

		// Prepare starting point
		color[start] = c.GREY;
		dist[start] = 0;
		queue.add(start);

		// Start searching
		while (!queue.isEmpty()) {
			int currentVertex = queue.remove();
			LinkedList<Integer> adjNodes = (LinkedList<Integer>) graph.adjacentNodes(currentVertex);

			// Look at all adjacent nodes and change them, if no previous
			// changes were made
			for (int i = 0; i < adjNodes.size(); i++) {
				int adjNode = adjNodes.get(i);

				if (color[adjNode] == c.WHITE) {
					color[adjNode] = c.GREY;
					dist[adjNode] = dist[currentVertex] + 1;
					pred[adjNode] = currentVertex;
					queue.add(adjNode);
				}
			}
			color[currentVertex] = c.BLACK;
		}
	}
}
