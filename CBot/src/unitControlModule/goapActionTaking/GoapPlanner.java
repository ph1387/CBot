package unitControlModule.goapActionTaking;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.JApplet;

import org.jgraph.JGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

// TODO: REMOVE public
public class GoapPlanner extends JApplet {

	// TODO: REMOVE until "Functions" comment
	public void init() {
		
		long startTime = System.nanoTime();
		SimpleDirectedWeightedGraph<GraphNode, DefaultWeightedEdge> graph = test(new OwnUnitWrapper());
		long endTime = System.nanoTime();

		long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
		System.out.println("\n\n\n" + duration + "ns - " + duration/1000000 + "ms");
		
		// Create Model Adapter
		JGraphModelAdapter<GraphNode, DefaultWeightedEdge> modelAdapter = new JGraphModelAdapter<GraphNode, DefaultWeightedEdge>(graph);
	
		// Import the model adapter to jgraph
		JGraph jgraph = new JGraph(modelAdapter);
		
		// Show the content on a japplet
		getContentPane().add(jgraph);
	}
	
	public SimpleDirectedWeightedGraph<GraphNode, DefaultWeightedEdge> test(GoapUnit goapUnit) {
		sortGoalStates(goapUnit);

		GraphNode startNode = new GraphNode(null);
		List<GraphNode> endNodes = new ArrayList<GraphNode>();
		
		SimpleDirectedWeightedGraph<GraphNode, DefaultWeightedEdge> graph = createGraph(goapUnit, startNode, endNodes);
		
		System.out.println(searchGraphForActionQueue(graph, startNode, endNodes));
		
		return graph;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// -------------------- Functions

	/**
	 * Generate a plan (Queue of GoapActions) which is then performed by the
	 * assigned GoapUnit. The planner uses the JGraphT library to create a
	 * directed-weighted graph and searches the branches with either A* or
	 * Dijkstra to find the shortest path to a units goal(s).
	 * 
	 * @param goapUnit
	 *            the GoapUnit the plan gets generated for.
	 * @return a generated plan (Queue) of GoapActions, that the GoapUnit has to
	 *         perform to archive the desired goalState OR null, if no plan was
	 *         generated.
	 */
	static Queue<GoapAction> plan(GoapUnit goapUnit) {
		Queue<GoapAction> createdPlan = null;

		try {
			sortGoalStates(goapUnit);

			GraphNode startNode = new GraphNode(null);
			List<GraphNode> endNodes = new ArrayList<GraphNode>();

			createdPlan = searchGraphForActionQueue(createGraph(goapUnit, startNode, endNodes), startNode, endNodes);
		} catch (Exception e) {
			e.printStackTrace();

			// TODO: Maybe add a System.out
		}
		return createdPlan;
	}
	
	// ------------------------------ Sort the goals
	
	/**
	 * Function for sorting a goapUnits goalStates (descending). The most
	 * important goal has the highest importance value.
	 *
	 * @param goapUnit
	 *            the GoapUnit which goals are being sorted.
	 * @return the sorted goal list of the goapUnit.
	 */
	private static List<GoapState> sortGoalStates(GoapUnit goapUnit) {
		if (goapUnit.getGoalState().size() > 1) {
			goapUnit.getGoalState().sort(new Comparator<GoapState>() {

				@Override
				public int compare(GoapState o1, GoapState o2) {
					return o2.importance.compareTo(o1.importance);
				}
			});
		}
		return goapUnit.getGoalState();
	}
	
	// ------------------------------ Create a graph
	
	/**
	 * Function to create a graph based on all possible unit actions of the
	 * GoapUnit.
	 *
	 * @param goapUnit
	 *            the GoapUnit the plan gets generated for.
	 * @param startNode
	 *            a Reference to the starting node to minimize search time.
	 * @param endNodes
	 *            a list for all end nodes, which are being created to minimize
	 *            the search time later.
	 * @return a directedWeightedGraph generated from all possible unit actions
	 *         for a goal.
	 */
	private static SimpleDirectedWeightedGraph<GraphNode, DefaultWeightedEdge> createGraph(GoapUnit goapUnit,
			GraphNode startNode, List<GraphNode> endNodes) {
		SimpleDirectedWeightedGraph<GraphNode, DefaultWeightedEdge> generatedGraph = new SimpleDirectedWeightedGraph<GraphNode, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);

		addVertices(generatedGraph, goapUnit, startNode, endNodes);
		addEdges(generatedGraph, goapUnit, startNode, endNodes);

		return generatedGraph;
	}
	
	// ---------------------------------------- Vertices	
	
	// TODO: Add to UML
	/**
	 * Function for adding vertices to a graph.
	 *
	 * @param graph
	 *            the graph the vertices are being added to.
	 * @param goapUnit
	 *            the unit whose worldState, goalStates and actions are being
	 *            added to the graph as nodes.
	 * @param startNode
	 *            a Reference to the starting node to minimize search time.
	 * @param endNodes
	 *            a list for all end nodes, which are being created to minimize
	 *            the search time later.
	 */
	private static void addVertices(SimpleDirectedWeightedGraph<GraphNode, DefaultWeightedEdge> graph,
			GoapUnit goapUnit, GraphNode startNode, List<GraphNode> endNodes) {
		// The effects from the world state as well as the precondition of the
		// goal have to be set at the beginning, since these are the effects the
		// unit tries to archive with its actions.
		GraphNode start = new GraphNode(null, goapUnit.getWorldState());
		startNode.overwriteOwnProperties(start);
		graph.addVertex(startNode);

		for (GoapState goalState : goapUnit.getGoalState()) {
			HashSet<GoapState> goalStateHash = new HashSet<GoapState>();
			goalStateHash.add(goalState);

			GraphNode end = new GraphNode(goalStateHash, null);
			graph.addVertex(end);
			endNodes.add(end);
		}

		HashSet<GoapAction> possibleActions = extractPossibleActions(goapUnit, goapUnit.getAvailableActions());

		// Afterward all other possible actions have to be added as well.
		if (possibleActions != null) {
			for (GoapAction goapAction : possibleActions) {
				graph.addVertex(new GraphNode(goapAction));
			}
		}
	}

	/**
	 * Needed to check if the available actions can actually be performed
	 * 
	 * @param goapUnit
	 *            the GoapUnit whose actions are being checked.
	 * @param availableActions
	 *            all GoapActions the unit can currently take.
	 * @return all possible actions which are actually available for the unit.
	 */
	private static HashSet<GoapAction> extractPossibleActions(GoapUnit goapUnit, HashSet<GoapAction> availableActions) {
		HashSet<GoapAction> possibleActions = new HashSet<GoapAction>();

		try {
			for (GoapAction goapAction : availableActions) {
				if (goapAction.checkProceduralPrecondition(goapUnit)) {
					possibleActions.add(goapAction);
				}
			}
		} catch (Exception e) {
			// TODO: Maybe add a System.out
		}
		return possibleActions;
	}
	
	// ---------------------------------------- Edges
	
	// TODO: Add to UML and Description
	private static void addEdges(SimpleDirectedWeightedGraph<GraphNode, DefaultWeightedEdge> graph, GoapUnit goapUnit, GraphNode startNode,
			List<GraphNode> endNodes) {
		int prevEdgeCount = 0;
		boolean edgesAdded = true;
		HashSet<GraphNode> nodesAlreadyConnectedOnce = new HashSet<GraphNode>();

		addDefaultEdges(graph, startNode, nodesAlreadyConnectedOnce);

		while(edgesAdded) {
			prevEdgeCount = graph.edgeSet().size();
			
			// Check each node against all already connected once nodes to find a possible match between the combined effects of the path + the worldState and the preconditions of the current node.
			for (GraphNode node : graph.vertexSet()) {
				// Select only node to which a path can be created (-> targets!)
				if(!node.equals(startNode)) {
					boolean addNodeToConnectedOnceList = tryToConnectNode(graph, goapUnit, startNode, endNodes, node, nodesAlreadyConnectedOnce);
					
					if(addNodeToConnectedOnceList && !nodesAlreadyConnectedOnce.contains(node)) {
						nodesAlreadyConnectedOnce.add(node);
					}
				}
			}
			
			if(prevEdgeCount == graph.edgeSet().size()) {
				edgesAdded = false;
			}
		}
		
		// TODO: REMOVE System.out
		for (GraphNode graphNode : graph.vertexSet()) {
			for (GraphPath<GraphNode, DefaultWeightedEdge> path : graphNode.pathsToThisNode) {
				System.out.println(path);
			}
			System.out.println("---");
		}
	}
	
	// TODO: Add to UML
	/**
	 * Function for adding the edges to the graph which are the connection from
	 * the starting node to all default accessible nodes (= actions). These
	 * nodes either have no precondition or their preconditions are all in the
	 * effect HashSet of the starting node. These default edges are needed since all further connections rely on them as the nodes can not connect to the starting node anymore.
	 *
	 * @param graph
	 *            the graph the edges are getting added to.
	 * @param startNode
	 *            the starting node which gets connected with the default
	 *            accessible nodes.
	 * @param nodesAlreadyConnectedOnce
	 *            the list of already connected nodes in which all nodes are
	 *            listed that are connected with the starting node.
	 */
	private static void addDefaultEdges(SimpleDirectedWeightedGraph<GraphNode, DefaultWeightedEdge> graph,
			GraphNode startNode, HashSet<GraphNode> nodesAlreadyConnectedOnce) {
		for (GraphNode graphNode : graph.vertexSet()) {
			if (!startNode.equals(graphNode) && graphNode.action != null && (graphNode.preconditions.isEmpty() || areAllPreconditionsMet(graphNode.preconditions, startNode.effects))) {	
					addEgdeWithWeigth(graph, startNode, graphNode, new DefaultWeightedEdge(), 0);
				if (!nodesAlreadyConnectedOnce.contains(graphNode)) {
					nodesAlreadyConnectedOnce.add(graphNode);
				}
				
				// Add the path to the node to the GraphPath list in the node since this is the first step inside the graph.
				List<GraphNode> vertices = new ArrayList<GraphNode>();
				List<DefaultWeightedEdge> edges = new ArrayList<DefaultWeightedEdge>();
				
				vertices.add(startNode);
				vertices.add(graphNode);
				
				edges.add(graph.getEdge(startNode, graphNode));
				
				GraphPath<GraphNode, DefaultWeightedEdge> graphPathToDefaultNode = new GraphWalk<GraphNode, DefaultWeightedEdge>(graph, startNode, graphNode, vertices, edges, graph.getEdgeWeight(graph.getEdge(startNode, graphNode)));
				graphNode.addGraphPath(graphPathToDefaultNode);
			}
		}
	}
	
	// TODO: Add to UML and Description
	// Test if all effects meet the preconditions.
	private static boolean areAllPreconditionsMet(HashSet<GoapState> preconditions, HashSet<GoapState> effects) {
		boolean preconditionsMet = true;
		
		for (GoapState precondition : preconditions) {
			for (GoapState effect : effects) {
				if(precondition.effect.equals(effect.effect) && !precondition.value.equals(effect.value)) {
					preconditionsMet = false;
					
					break;
				}
			}
			
			if(!preconditionsMet) {
				break;
			}
		}
		return preconditionsMet;
	}
	
	// TODO: Add to UML
	/**
	 * Convenience function for adding a weighted edge to an existing graph.
	 *
	 * @param graph
	 *            the graph the edge is added to.
	 * @param firstVertex
	 *            start vertex.
	 * @param secondVertex
	 *            target vertex.
	 * @param edge
	 *            edge reference.
	 * @param weight
	 *            the weight of the edge being created.
	 * @return true or false depending if the creation of the edge was
	 *         successful or not.
	 */
	private static <V, E> boolean addEgdeWithWeigth(SimpleDirectedWeightedGraph<V, E> graph, V firstVertex,
			V secondVertex, E edge, double weight) {
		try {
			graph.addEdge(firstVertex, secondVertex, edge);
			graph.setEdgeWeight(graph.getEdge(firstVertex, secondVertex), weight);

			return true;
		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}
	}
	
	// TODO: Add to UML and Description
	private static boolean tryToConnectNode(SimpleDirectedWeightedGraph<GraphNode, DefaultWeightedEdge> graph, GoapUnit goapUnit, GraphNode startNode,
			List<GraphNode> endNodes, GraphNode node, HashSet<GraphNode> nodesAlreadyConnectedOnce) {
		boolean connected = false;
		
		for (GraphNode nodeConnectedOnce : nodesAlreadyConnectedOnce) {
			// End nodes can not have a edge towards another node and the target node must not be itself. Also there must not already be an edge in the graph
			//  && !graph.containsEdge(nodeConnectedOnce, node) has to be added if the search for a path should stop if the node is already connected once with the target. This leads to the case where no alternative routes are being stored inside the pathsToThisNode list.
			if(!node.equals(nodeConnectedOnce) && !endNodes.contains(nodeConnectedOnce)) {
				
				// Every saved path to this node is checked if any of these produce a suitable effect set regarding the preconditions of the current node.
				for (GraphPath<GraphNode, DefaultWeightedEdge> pathToListNode : nodeConnectedOnce.pathsToThisNode) {
					HashSet<GoapState> combinedEffectsOnPath = addPathEffectsTogether(pathToListNode);

					if(areAllPreconditionsMet(combinedEffectsOnPath, node.preconditions)) {
						connected = true;

						addEgdeWithWeigth(graph, nodeConnectedOnce, node, new DefaultWeightedEdge(), nodeConnectedOnce.action.generateCost(goapUnit));
						
						node.addGraphPath(addNodeToGraphPath(graph, pathToListNode, node));
					}
				}
			}
		}
		return connected;
	}
	
	// TODO: Add to UML and Description
	// Effects of the path are added together.
	private static HashSet<GoapState> addPathEffectsTogether(GraphPath<GraphNode, DefaultWeightedEdge> path) {
		HashSet<GoapState> combinedNodeEffects = new HashSet<GoapState>();
		
		for (GraphNode pathNode : path.getVertexList()) {
			List<GoapState> statesToBeRemoved = new ArrayList<GoapState>();
			
			// Mark effects to be removed
			for (GoapState nodeWorldState : combinedNodeEffects) {
				for (GoapState pathNodeEffect : pathNode.effects) {
					if(nodeWorldState.effect.equals(pathNodeEffect.effect)) {
						statesToBeRemoved.add(nodeWorldState);
					}
				}
			}
			
			// Remove marked effects from the state
			for (GoapState stateToRemove : statesToBeRemoved) {
				combinedNodeEffects.remove(stateToRemove);
			}
			
			// Add all effects from the current node to the HashSet
			for (GoapState effect : pathNode.effects) {
				combinedNodeEffects.add(effect);
			}
		}
		return combinedNodeEffects;
	}
	
	// TODO: Add to UML and Description
	// Generate a new GraphPath with the new node at the end. All other values are either copied or copied and changed.
	private static GraphPath<GraphNode, DefaultWeightedEdge> addNodeToGraphPath(SimpleDirectedWeightedGraph<GraphNode, DefaultWeightedEdge> graph, GraphPath<GraphNode, DefaultWeightedEdge> baseGraphPath, GraphNode nodeToAdd) {
		double weight = baseGraphPath.getWeight();
		List<GraphNode> vertices = new ArrayList<GraphNode>(baseGraphPath.getVertexList());
		List<DefaultWeightedEdge> edges = new ArrayList<DefaultWeightedEdge>(baseGraphPath.getEdgeList());
		
		if(nodeToAdd.action != null) {
			weight += graph.getEdgeWeight(graph.getEdge(baseGraphPath.getEndVertex(), nodeToAdd));
		}
		
		vertices.add(nodeToAdd);
		edges.add(graph.getEdge(baseGraphPath.getEndVertex(), nodeToAdd));
		
		return new GraphWalk<GraphNode, DefaultWeightedEdge>(graph, baseGraphPath.getStartVertex(), nodeToAdd, vertices, edges, weight);
	}

	// ------------------------------ Search the graph for a Queue of GoapActions
	
	/**
	 * Function for searching a graph for the lowest cost of a series of actions
	 * which have to be taken to archive a certain goal which has most certainly the highest importance.
	 *
	 * @param graph
	 *            the graph of GoapActions the unit has to take in order to
	 *            archive a goal.
	 * @param startNode
	 *            a Reference to the starting node to minimize search time.
	 * @param endNodes
	 *            a list of all end nodes to minimize search time.
	 * @return the Queue of GoapActions which has the lowest cost to archive a
	 *         goal.
	 */
	private static Queue<GoapAction> searchGraphForActionQueue(SimpleDirectedWeightedGraph<GraphNode, DefaultWeightedEdge> graph,
			GraphNode startNode, List<GraphNode> endNodes) {
		Queue<GoapAction> actionQueue = null;

		for (int i = 0; i < endNodes.size() && actionQueue == null; i++) {
			sortPathsLeadingToNode(endNodes.get(i));
			
			for (int j = 0; j < endNodes.get(i).pathsToThisNode.size() && actionQueue == null; j++) {
				actionQueue = extractActionsFromGraphPath(endNodes.get(i).pathsToThisNode.get(j), startNode, endNodes.get(i));
			}
		}
		return actionQueue;
	}
	
	// TODO: Add to UML and Description
	private static void sortPathsLeadingToNode(GraphNode node) {
		node.pathsToThisNode.sort(new Comparator<GraphPath>() {

			@Override
			public int compare(GraphPath o1, GraphPath o2) {
				return ((Double)o1.getWeight()).compareTo(o2.getWeight());
			}
		});
	}
	
	// TODO: Add to UML and Description
	private static Queue<GoapAction> extractActionsFromGraphPath(GraphPath<GraphNode, DefaultWeightedEdge> path, GraphNode startNode, GraphNode endNode) {
		Queue<GoapAction> actionQueue = new LinkedList<GoapAction>();
		
		for (GraphNode node : path.getVertexList()) {
			if(!node.equals(startNode) && !node.equals(endNode) ) {
				actionQueue.add(node.action);
			}
		}
		return actionQueue;
	}
}
