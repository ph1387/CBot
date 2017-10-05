package buildingOrderModule.simulator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.TreeSet;
import java.util.function.BiConsumer;

import bwapi.Pair;

/**
 * Simulator.java --- A Simulator for simulating the future possibilities of
 * provided actions with a collection of available / free (Unit-) Types and busy
 * / occupied ones.
 * 
 * @author P H - 05.07.2017
 *
 */
public class Simulator {

	private static final int DEFAULT_INITIAL_NODE_COUNT = 100000;
	private static final int DEFAULT_INITIAL_ACTION_SEQUENCE_COUNT = 100000;

	private HashSet<Node> nodes = new HashSet<>();
	private TreeSet<Node> currentLayerNodes = new TreeSet<>();
	private NodeFactory nodeFactory;
	private ActionSequenceFactory sequenceFactory;

	// Worker specific gathering information:
	// Single Worker speed:
	// ~50 minerals per minute = ~50 minerals per 1440 frames = ~0.0347 minerals
	// per frame
	private double mineralsPerFrame = 0.0347;
	// ~288 gas per minute (3 workers) = ~288 gas per 1440 frames = ~0.2 gas per
	// frame
	private double gasPerFrame = 0.2;

	// Available Actions = Units, Buildings, Technologies etc.
	private HashSet<ActionType> actionTypes;
	// The amount each ActionType may be used in the simulation.
	private HashMap<ActionType, Integer> maxActionTypesOccurrences;

	// TODO: UML CHANGE 100
	// The maximum number of Nodes in a layer.
	private int layerNodesMaxCount = 10;
	// The maximum size of an action sequence. Necessary since the while loop
	// could otherwise lead to extremely long cycles.
	private int actionSequenceMaxSize = 6;

	/**
	 * @param actionTypes
	 *            the Actions that are being considered in the generation of the
	 *            tree.
	 */
	public Simulator(HashSet<ActionType> actionTypes) {
		this(actionTypes, DEFAULT_INITIAL_NODE_COUNT, DEFAULT_INITIAL_ACTION_SEQUENCE_COUNT);
	}

	/**
	 * @param actionTypes
	 *            the Actions that are being considered in the generation of the
	 *            tree.
	 * @param initialNodeCount
	 *            the amount of Nodes that are initially being stored in the
	 *            {@link NodeFactory}.
	 * @param initialActionSequenceCount
	 *            the amount of ActionSequences that are initially being stored
	 *            in the {@link ActionSequenceFactory}.
	 */
	public Simulator(HashSet<ActionType> actionTypes, int initialNodeCount, int initialActionSequenceCount) {
		this.actionTypes = actionTypes;
		this.nodeFactory = new NodeFactory(initialNodeCount);
		this.sequenceFactory = new ActionSequenceFactory(initialActionSequenceCount);
	}

	// -------------------- Functions

	// TODO: Possible Change: Make generic for further use.
	/**
	 * Function for performing a simulation based on available ActionTypes and
	 * provided resources (minerals and gas) as well as a record of available
	 * and occupied / busy TypeWrappers. This simulation generates a tree with
	 * each layer representing an iteration / certain step in the time line with
	 * a predefined step size. The number of possible Nodes each Node of the
	 * previous layer can produce depends on the number of available ActionTypes
	 * as well as the available / free Types since the Actions require a free
	 * type of Unit to be able to be executed and therefore taken in
	 * consideration. This simulation is quite expensive since it takes various
	 * factors like the availability of certain Types and the different
	 * permutations of all possible ActionType sequences in consideration to
	 * produce the "optimal" result at the end of the simulation. These results
	 * may vary since the frame step size and the amount of steps being taken
	 * have a huge impact on it.
	 * 
	 * @param currentFrameTimeStamp
	 *            the time stamp in frames at which the simulation starts.
	 * @param frameStep
	 *            the step in frames the simulation takes in each iteration.
	 * @param stepAmount
	 *            the amount of steps being taken in total (=> Iteration count).
	 * @param currentMinerals
	 *            the current amount of available minerals.
	 * @param currentGas
	 *            the current amount of available gas.
	 * @param typesFree
	 *            the currently available Types in form of a HashMap: Key =
	 *            Type, Integer = Amount of free Types.
	 * @param typesWorking
	 *            the currently occupied Types in form of a HashMap: Key = Type,
	 *            ArrayList = Pairs of resulting TypeWrappers and the time stamp
	 *            at which they are finished.
	 * @param workerType
	 *            the type of worker that is responsible for gathering minerals
	 *            and gas.
	 * @param idleScorePenalty
	 *            the score penalty for not taking any Actions in an iteration.
	 * @param consecutiveActionsBonus
	 *            the score bonus for taking multiple Actions at once.
	 * @param allowIdle
	 *            true or false depending if the creation of idle Nodes (not
	 *            taking any Actions) in the tree is allowed or not.
	 * @return the best sequence of Actions of the tree with the highest score.
	 */
	public ArrayList<ActionType> simulate(int currentFrameTimeStamp, int frameStep, int stepAmount, int currentMinerals,
			int currentGas, HashMap<TypeWrapper, Integer> typesFree,
			HashMap<TypeWrapper, ArrayList<Pair<TypeWrapper, Integer>>> typesWorking, TypeWrapper workerType,
			int idleScorePenalty, int consecutiveActionsBonus, boolean allowIdle) {
		// Create the root and save it.
		Node root = this.createRoot(currentMinerals, currentGas, currentFrameTimeStamp, typesFree, typesWorking);
		this.currentLayerNodes.add(root);
		this.nodes.add(root);

		// Extract the maximum number of occurrences of each ActionType.
		this.maxActionTypesOccurrences = this.extractMaxActionTypeOccurences();

		// Each iteration a new layer is formed. Each layer simulates a state in
		// the future:
		// => Current frame count + i * amount of frames being skipped.
		for (int i = 0; i < stepAmount; i++) {
			int simulatedFrameTimeStamp = currentFrameTimeStamp + i * frameStep;
			int treeSpaceAvailable = this.layerNodesMaxCount;

			// Collection for the new branch layer on top of the current one. A
			// TreeSet is being used since it provides a sorted insertion which
			// in combination with not allowing the insertion of the same
			// element twice is a lot faster than a simple List.
			// -> Massive performance improvement!
			TreeSet<Node> newLayerNodes = new TreeSet<Node>();

			// Simulated resource gathering:
			int simulatedMineralGain = this.generateSimulatedMineralGain(typesFree, typesWorking, workerType,
					frameStep);
			int simulatedGasGain = 0;
			// Only simulate gas gain if at least one single refinery was built
			// before. Multiply the gas gain by the amount of refineries
			// constructed.
			if (typesFree.containsKey(TypeWrapper.UnitType_Terran_Refinery)) {
				simulatedGasGain = (int) (typesFree.get(TypeWrapper.UnitType_Terran_Refinery) * frameStep
						* this.gasPerFrame);
			}

			// Free all Units whose ActionType timeStamp lie in the past.
			if (i != 0) {
				this.tryFreeingFinishedActions(this.currentLayerNodes, simulatedFrameTimeStamp);
			}

			// Iterate through all current branch Nodes and create another
			// branch layer.
			for (Node node : this.currentLayerNodes) {
				HashSet<ActionSequence> actionTypeSequences = this.generateAllPossibleActionTypeSequences(node,
						simulatedFrameTimeStamp, treeSpaceAvailable);
				// Update the available space in the tree.
				treeSpaceAvailable -= actionTypeSequences.size();

				// Transfer a copy of the current Node into the next layer. This
				// simulates the process of not taking any action in this
				// iteration.
				if (allowIdle) {
					newLayerNodes.add(this.generateIdleNode(node, simulatedMineralGain, simulatedGasGain,
							simulatedFrameTimeStamp, stepAmount, i, idleScorePenalty));
				}

				// Each sequence is a new Node in the next layer.
				for (ActionSequence sequence : actionTypeSequences) {
					// Generate a new Node in the layer and configure it
					// accordingly to the received values from the sequence
					// generation.
					Node nodeToAdd = this.generateNewLayerNode(node, simulatedMineralGain, simulatedGasGain,
							simulatedFrameTimeStamp, consecutiveActionsBonus, sequence);

					// If the Node is not added to the collection of Nodes then
					// make it available again.
					if (newLayerNodes.contains(nodeToAdd) || !newLayerNodes.add(nodeToAdd)) {
						this.nodeFactory.markNodeAsAvailable(nodeToAdd);
					}

					// Make the instance of the ActionSequence available again.
					this.sequenceFactory.markSequenceAsAvailable(sequence);
				}

				// No need to continue the loop.
				if (treeSpaceAvailable <= 0) {
					break;
				}
			}

			// Carry over to the newly created layer of Nodes.
			this.nodes.addAll(newLayerNodes);
			this.currentLayerNodes = newLayerNodes;
			this.removeNodesUntilSizeMatches(this.currentLayerNodes);
		}

		// Extract the best sequence of ActionTypes from the root of the tree
		// towards the Node with the highest score in the current layer.
		ArrayList<ActionType> bestActionTypeSequence = this.extractActionTypeSequence(this.currentLayerNodes.first());

		// Return all Nodes to the NodeFactory.
		this.nodeFactory.markNodesAsAvailable(this.nodes);
		this.currentLayerNodes.clear();
		this.nodes.clear();

		// Return the sequence of ActionTypes with the highest score.
		return bestActionTypeSequence;
	}

	/**
	 * Function for extracting the maximum number of times a ActionType may
	 * appear in a complete simulation iteration. This means a specific
	 * ActionType can not appear more than the specified number of times in a
	 * finished ActionType sequence.
	 * 
	 * @return a HashMap containing the maximum number of times (Value) a
	 *         ActionType (Key) may appear. If a value can be added indefinitely
	 *         the HashMap does contain Integer.MAX_VALUE as stored value.
	 */
	private HashMap<ActionType, Integer> extractMaxActionTypeOccurences() {
		HashMap<ActionType, Integer> occurrences = new HashMap<>();

		// Extract the maximum number of times each ActionType may be added to a
		// sequence.
		for (ActionType actionType : this.actionTypes) {
			// Differentiate between a set amount of times and undefined.
			if (actionType.defineMaxSimulationOccurrences() >= 0) {
				occurrences.put(actionType, actionType.defineMaxSimulationOccurrences());
			} else {
				occurrences.put(actionType, Integer.MAX_VALUE);
			}
		}
		return occurrences;
	}

	/**
	 * Function for extracting the sequence of ActionTypes from a Node and the
	 * path starting at the root towards it.
	 * 
	 * @param node
	 *            the Node to which the ActionType sequence will be extracted.
	 * @return the sequence of ActionTypes that were taken along the path to and
	 *         at the provided Node. The smaller the index, the earlier the
	 *         Action was taken.
	 */
	private ArrayList<ActionType> extractActionTypeSequence(Node node) {
		ArrayList<ActionType> actionTypeSequence = new ArrayList<>();
		List<Node> branch = this.extractPathFromRootToNode(node);

		// Extract the ActionTypes in each Node.
		for (Node currentNode : branch) {
			// TODO: DEBUG INFO
			System.out.println(" Node Score: " + currentNode.getScore());

			if (!currentNode.getChosenActions().isEmpty()) {
				// TODO: DEBUG INFO
				System.out.println(" Sequence start:");

				for (ActionType actionType : currentNode.getChosenActions()) {
					// TODO: DEBUG INFO
					System.out.println("  - " + actionType.getClass().getSimpleName());
					actionTypeSequence.add(actionType);
				}
			}
		}

		return actionTypeSequence;
	}

	/**
	 * Function for extracting the path from the root of a / the tree towards a
	 * provided Node.
	 * 
	 * @param node
	 *            the target Node to which the path will be generated.
	 * @return an List with the root at index 0 and the provided target Node at
	 *         index List.length - 1.
	 */
	private List<Node> extractPathFromRootToNode(Node node) {
		ArrayList<Node> branch = new ArrayList<>();
		Node currentLeafNode = node;

		// Move down the tree until the root is hit.
		while (currentLeafNode != null) {
			branch.add(0, currentLeafNode);
			currentLeafNode = currentLeafNode.getPreviousNode();
		}

		return branch;
	}

	/**
	 * Function for generating the root of a tree that the Simulator is going to
	 * be using.
	 * 
	 * @param currentMinerals
	 *            the available minerals in the beginning.
	 * @param currentGas
	 *            the available gas in the beginning.
	 * @param currentFrameTimeStamp
	 *            the TimeStamp in frames that the root is starting at.
	 * @param typesFree
	 *            the currently available Types that can perform actions.
	 * @param typesWorking
	 *            the currently unavailable Types that are busy working /
	 *            occupied.
	 * @return a Node that the Simulator can use as root for a generated tree.
	 */
	private Node createRoot(int currentMinerals, int currentGas, int currentFrameTimeStamp,
			HashMap<TypeWrapper, Integer> typesFree,
			HashMap<TypeWrapper, ArrayList<Pair<TypeWrapper, Integer>>> typesWorking) {
		// Create the root of the tree.
		Node root = this.nodeFactory.receiveNode();

		root.setCurrentMinerals(currentMinerals);
		root.setCurrentGas(currentGas);
		root.setFrameTimeStamp(currentFrameTimeStamp);
		root.setTypesFree(typesFree);
		root.setTypesWorking(typesWorking);

		// Set the occurrence to 0 for all defined ActionTypes.
		for (ActionType actionType : this.actionTypes) {
			root.getActionTypeOccurrences().put(actionType, 0);
		}

		return root;
	}

	/**
	 * Function for generating the resource values the working Units are going
	 * to produce in the simulated time.
	 * 
	 * @param unitsFree
	 *            the currently available Types.
	 * @param typesWorking
	 *            the currently unavailable / busy Types.
	 * @param workerType
	 *            the type that represents the worker in the simulation and
	 *            therefore gathers resources.
	 * @param frameStep
	 *            the amount of frames the Simulator takes at one time.
	 * @return the amount of minerals the specified worker Units are gathering.
	 */
	private int generateSimulatedMineralGain(HashMap<TypeWrapper, Integer> unitsFree,
			HashMap<TypeWrapper, ArrayList<Pair<TypeWrapper, Integer>>> typesWorking, TypeWrapper workerType,
			int frameStep) {
		// Count all workers. This is not completely accurate since workers can
		// not collect
		int totalWorkerCount = 0;
		if (unitsFree.get(workerType) != null) {
			totalWorkerCount += unitsFree.get(workerType);
		}
		if (typesWorking.get(workerType) != null) {
			totalWorkerCount += typesWorking.get(workerType).size();
		}

		return (int) (frameStep * (this.mineralsPerFrame * totalWorkerCount));
	}

	/**
	 * Function for finishing up and removing ActionTypes from a collection of
	 * Nodes. The requirement for removing an ActionType is that the time stamp
	 * that specifies the completion time of the specific Action lies in the
	 * past.
	 * 
	 * @param nodes
	 *            the Nodes whose ActionTypes are being finished up.
	 * @param simulatedFrameTimeStamp
	 *            the amount of frames the Simulator skips in one single step.
	 */
	private void tryFreeingFinishedActions(Collection<Node> nodes, int simulatedFrameTimeStamp) {
		for (Node node : nodes) {
			// Free any finished simulated Types from their work if the
			// timeStamp of their actions lies in the past.
			ArrayList<Pair<TypeWrapper, Integer>> unitsToFree = this.findFinishedSimulatedUnits(node,
					simulatedFrameTimeStamp);

			// Free the simulated Units from their task and add the newly
			// created ones to the List of available Units.
			// Pair.first = Type that creates something.
			// Pair.second = TimeStamp of the thing created.
			for (Pair<TypeWrapper, Integer> pair : unitsToFree) {
				Pair<TypeWrapper, Integer> pairToRemove = null;

				// Find the Pair with the specified timeStamp in all the
				// possible Pairs.
				for (Pair<TypeWrapper, Integer> possiblePair : node.getTypesWorking().get(pair.first)) {
					if (possiblePair.second.equals(pair.second)) {
						pairToRemove = possiblePair;

						break;
					}
				}

				// If the Pair was found, remove it from the ongoing Actions.
				if (pairToRemove != null) {
					node.getTypesFree().put(pair.first, node.getTypesFree().get(pair.first) + 1);
					node.getTypesWorking().get(pair.first).remove(pairToRemove);
				}
			}
		}
	}

	/**
	 * Function for finding and extracting the ActionTypes in a single Node
	 * whose completion times lie in the past.
	 * 
	 * @param node
	 *            the Node whose ActionType completion times are being checked.
	 * @param simulatedFrameTimeStamp
	 *            the amount of frames the Simulator skips in one single step.
	 * @return a ArrayList of Pairs. These Pairs contain the finished types and
	 *         the corresponding time stamps. Latter is required for
	 *         identification. </br>
	 *         </br>
	 *         Pair.first = Type that creates something.</br>
	 *         Pair.second = TimeStamp of the thing created.
	 */
	private ArrayList<Pair<TypeWrapper, Integer>> findFinishedSimulatedUnits(final Node node,
			final int simulatedFrameTimeStamp) {
		// Pair.first = Type that creates something.
		// Pair.second = TimeStamp of the thing created.
		final ArrayList<Pair<TypeWrapper, Integer>> unitsToFree = new ArrayList<>();

		node.getTypesWorking().forEach(new BiConsumer<TypeWrapper, ArrayList<Pair<TypeWrapper, Integer>>>() {

			@Override
			public void accept(TypeWrapper typeWrapper, ArrayList<Pair<TypeWrapper, Integer>> list) {
				for (Pair<TypeWrapper, Integer> pair : list) {
					// Extract all Types whose actions are finished.
					if (pair.second <= simulatedFrameTimeStamp) {
						unitsToFree.add(new Pair<>(typeWrapper, pair.second));

						// Add the finished Types to the other available
						// Types.
						if (node.getTypesFree().get(pair.first) == null) {
							node.getTypesFree().put(pair.first, 0);
						} else {
							node.getTypesFree().put(pair.first, node.getTypesFree().get(pair.first) + 1);
						}
					}
				}
			}
		});

		return unitsToFree;
	}

	/**
	 * Function for generating an Node based on another super node. This Node is
	 * nearly an exact copy of the provided one except no ActionTypes are being
	 * taken and the mineral and gas counts are increased.
	 * 
	 * @param parentNode
	 *            the Node the one being created is based on.
	 * @param simulatedMineralGain
	 *            the mineral gain the Node is experiencing in one single
	 *            iteration step.
	 * @param simulatedGasGain
	 *            the gas gain the Node is experiencing in one single iteration
	 *            step.
	 * @param simulatedFrameTimeStamp
	 *            the iteration step in frames.
	 * @param stepAmount
	 *            the total amount of iterations.
	 * @param iterationCount
	 *            the index of the current iteration (starting at 0).
	 * @param idleScorePenalty
	 *            the penalty for not taking any Actions.
	 * @return a new Node based on a provided one with no Actions taken and the
	 *         simulated values attached.
	 */
	private Node generateIdleNode(Node parentNode, int simulatedMineralGain, int simulatedGasGain,
			int simulatedFrameTimeStamp, int stepAmount, int iterationCount, int idleScorePenalty) {
		Node newNode = this.nodeFactory.receiveNode();
		newNode.setCurrentMinerals(parentNode.getCurrentMinerals() + simulatedMineralGain);
		newNode.setCurrentGas(parentNode.getCurrentGas() + simulatedGasGain);
		newNode.setFrameTimeStamp(simulatedFrameTimeStamp);
		newNode.setPreviousNode(parentNode);

		// No Actions were being taken.
		newNode.setChosenActions(new ArrayList<ActionType>());

		// The penalty for not taking any actions is far greater in the
		// beginning than it is in the end. Therefore action sequences are
		// prioritized that initially provide actions and might idle later
		// instead of sequences idling in the beginning and taking actions in
		// the end.
		newNode.setScore(parentNode.getScore() - ((stepAmount - iterationCount) * idleScorePenalty));

		// Transfer the occupied and free Types to the new Node.
		newNode.setTypesWorking(new HashMap<>(parentNode.getTypesWorking()));
		newNode.setTypesFree(new HashMap<>(parentNode.getTypesFree()));

		// Transfer the counter for each ActionType to the new Node.
		newNode.setActionTypeOccurrences(new HashMap<>(parentNode.getActionTypeOccurrences()));

		return newNode;
	}

	/**
	 * Function for generating an Node based on another super Node. This
	 * function takes a sequence of ActionTypes that represent the Actions being
	 * taken in the current iteration as well as the simulated mineral and gas
	 * gains that are projected onto the total amount of resources available.
	 * 
	 * @param parentNode
	 *            the Node the one being created is based on.
	 * @param simulatedMineralGain
	 *            the mineral gain the Node is experiencing in one single
	 *            iteration step.
	 * @param simulatedGasGain
	 *            the gas gain the Node is experiencing in one single iteration
	 *            step.
	 * @param simulatedFrameTimeStamp
	 *            the iteration step in frames.
	 * @param consecutiveActionsBonus
	 *            the extra score for taking multiple Actions at once.
	 * @param sequence
	 *            the sequence of Actions being taken in this case.
	 * @return a new Node that stores all important information and is based on
	 *         a super Node.
	 */
	private Node generateNewLayerNode(Node parentNode, int simulatedMineralGain, int simulatedGasGain,
			int simulatedFrameTimeStamp, int consecutiveActionsBonus, ActionSequence sequence) {
		Node newNode = this.nodeFactory.receiveNode();
		newNode.setCurrentMinerals(parentNode.getCurrentMinerals() - sequence.getMineralCost() + simulatedMineralGain);
		newNode.setCurrentGas(parentNode.getCurrentGas() - sequence.getGasCost() + simulatedGasGain);
		newNode.setFrameTimeStamp(simulatedFrameTimeStamp);
		newNode.setPreviousNode(parentNode);
		newNode.setChosenActions(new ArrayList<>(sequence.getActionTypeSequence()));
		// Set the score of the newly created Node to the score of the previous
		// Node plus the value of the actions being taken in this instance as
		// well as a provided bonus for consecutive actions.
		newNode.setScore(parentNode.getScore() + newNode.generateScoreOfActions()
				+ (newNode.getChosenActions().size() * consecutiveActionsBonus));

		// Transfer the occupied and free Types to the new Node.
		newNode.setTypesWorking(new HashMap<>(sequence.getOccupiedTypeTimes()));
		newNode.setTypesFree(new HashMap<>(sequence.getTypesFree()));

		// Transfer the counter for each ActionType to the new Node.
		newNode.setActionTypeOccurrences(new HashMap<>(parentNode.getActionTypeOccurrences()));

		// Increase the counter for each used ActionType in the HashMap.
		for (ActionType actionType : sequence.getActionTypeSequence()) {
			newNode.getActionTypeOccurrences().put(actionType, newNode.getActionTypeOccurrences().get(actionType) + 1);
		}

		return newNode;
	}

	/**
	 * Function for removing elements from a provided TreeSet containing Nodes.
	 * The elements that are going to be removed are the ones at the end of the
	 * TreeSet and therefore the ones with the lowest score. Leaving only the
	 * "best" ones behind.
	 * 
	 * @param nodes
	 *            the TreeSet from which Nodes are removed.
	 */
	private void removeNodesUntilSizeMatches(TreeSet<Node> nodes) {
		int nodesToRemove = nodes.size() - this.layerNodesMaxCount;

		// Remove a fixed amount of Nodes from the end of the TreeSet leaving
		// only the "best" ones behind.
		for (int i = 0; i < nodesToRemove; i++) {
			nodes.pollLast();
		}
	}

	/**
	 * Function for generating all possible permutations of ActionTypes a Node
	 * can perform. This takes the available mineral and gas counts in
	 * consideration as well as the fact that the order of the Actions being
	 * taken does not matter ((1, 2) == (2, 1) => {1, 2}).
	 * 
	 * @param currentNode
	 *            the Node whose ActionTypes are the base for the generation of
	 *            the possible permutations.
	 * @param simulatedTimeStamp
	 *            the time stamp in frames that simulates a certain moment in
	 *            the future.
	 * @param maxSequenceCount
	 *            the maximum number of sequences that this function may
	 *            generate. Needed for limiting the output and decreasing the
	 *            complexity.
	 * @return a Set of all possible permutations that are the result of the
	 *         ActionTypes being taken based on the provided Node.
	 */
	private HashSet<ActionSequence> generateAllPossibleActionTypeSequences(Node currentNode, int simulatedTimeStamp,
			int maxSequenceCount) {
		HashSet<ActionSequence> actionSequences = new HashSet<>();
		HashSet<ActionType> possibleActionTypes = new HashSet<>();
		Queue<ActionSequence> workingSets = new LinkedList<>();

		// Find the ActionTypes that are actually usable with the current
		// state of the Node and add them to the Queue of ActionType sequences
		// that are being processed.
		this.findUsableActionTypes(currentNode, simulatedTimeStamp, possibleActionTypes, workingSets);

		// Work on the Queue of ActionType sequences until all executable
		// permutations are found.
		this.findAllPossibleActionTypeCombinations(currentNode, simulatedTimeStamp, possibleActionTypes, workingSets,
				actionSequences, maxSequenceCount);

		return actionSequences;
	}

	/**
	 * Function for finding the initial ActionTypes that can be executed. These
	 * ActionTypes are one by one transformed into a ActionSequence and then
	 * added to the Queue of ActionSequences to be worked on. Also each
	 * ActionType which can be executed at the current moment (-> Simulated Node
	 * timeStamp and state in the future) is added to a Set of executable
	 * ActionTypes for later use.
	 * 
	 * @param currentNode
	 *            the Node against whose state all possible ActionTypes are
	 *            checked.
	 * @param simulatedTimeStamp
	 *            the timeStamp in the simulated future in frames.
	 * @param possibleActionTypes
	 *            the Set to which all ActionTypes are added whose execution is
	 *            possible.
	 * @param workingSets
	 *            the Queue to which the initial ActionSequences are added.
	 */
	private void findUsableActionTypes(Node currentNode, int simulatedTimeStamp,
			HashSet<ActionType> possibleActionTypes, Queue<ActionSequence> workingSets) {
		for (ActionType actionType : this.actionTypes) {
			// Costs and preconditions must apply before adding the ActionType
			// towards the already existing sequence of Actions.
			if (currentNode.getTypesFree().get(actionType.defineRequiredType()) != null
					&& currentNode.getTypesFree().get(actionType.defineRequiredType()) > 0
					&& currentNode.getCurrentMinerals() >= actionType.defineMineralCost()
					&& currentNode.getCurrentGas() >= actionType.defineGasCost()) {
				// Since each ActionType may only be used a certain amount in a
				// complete path along the tree (number of following Nodes) the
				// total number of ActionTypes used for this particular
				// (parent-) Node are being compared. Only ActionTypes below the
				// threshold may be used.
				boolean belowThreshold = currentNode.getActionTypeOccurrences()
						.get(actionType) < this.maxActionTypesOccurrences.get(actionType);

				if (belowThreshold) {
					workingSets.add(this.generateNewActionSequence(currentNode, actionType, simulatedTimeStamp));

					// Save the ActionType for later -> Saves time looking up
					// Actions!
					possibleActionTypes.add(actionType);
				}
			}
		}
	}

	/**
	 * Function for generating a ActionSequence based on a provided Node, a
	 * generated future time stamp and the ActionType that is being added to the
	 * sequence of Actions being taken.
	 * 
	 * @param node
	 *            the Node the ActionSequence is based on.
	 * @param actionType
	 *            the type of Action that is being added to the already existing
	 *            sequence of Actions being taken.
	 * @param simulatedTimeStamp
	 *            the time stamp of a simulated moment in the future that the
	 *            completion time of the Action being taken is added onto.
	 * @return a ActionSequence based on the ActionSequence of a provided Node
	 *         with the addition of another ActionType.
	 */
	private ActionSequence generateNewActionSequence(Node node, ActionType actionType, int simulatedTimeStamp) {
		final ActionSequence actionSequence = this.sequenceFactory.receiveSequence();

		// Sequence of ActionTypes being taken.
		actionSequence.getActionTypeSequence().add(actionType);

		// Costs.
		actionSequence.setMineralCost(actionType.defineMineralCost());
		actionSequence.setGasCost(actionType.defineGasCost());

		// Completion time and free Units.
		actionSequence.setOccupiedTypeTimes(new HashMap<TypeWrapper, ArrayList<Pair<TypeWrapper, Integer>>>());
		node.getTypesWorking().forEach(new BiConsumer<TypeWrapper, ArrayList<Pair<TypeWrapper, Integer>>>() {

			@Override
			public void accept(TypeWrapper typeWrapper, ArrayList<Pair<TypeWrapper, Integer>> list) {
				// Create a new List for the Type.
				if (actionSequence.getOccupiedTypeTimes().get(typeWrapper) == null) {
					actionSequence.getOccupiedTypeTimes().put(typeWrapper, new ArrayList<Pair<TypeWrapper, Integer>>());
				}

				ArrayList<Pair<TypeWrapper, Integer>> occupiedUnitsList = actionSequence.getOccupiedTypeTimes()
						.get(typeWrapper);

				// Insert each Pair from the given List into the newly created
				// one.
				for (Pair<TypeWrapper, Integer> pair : list) {
					occupiedUnitsList.add(pair);
				}
			}
		});
		if (actionSequence.getOccupiedTypeTimes().get(actionType.defineRequiredType()) == null) {
			actionSequence.getOccupiedTypeTimes().put(actionType.defineRequiredType(),
					new ArrayList<Pair<TypeWrapper, Integer>>());
		}
		actionSequence.getOccupiedTypeTimes().get(actionType.defineRequiredType())
				.add(new Pair<>(actionType.defineResultType(), simulatedTimeStamp + actionType.defineCompletionTime()));
		actionSequence.setTypesFree(new HashMap<>(node.getTypesFree()));
		actionSequence.getTypesFree().put(actionType.defineRequiredType(),
				node.getTypesFree().get(actionType.defineRequiredType()) - 1);

		return actionSequence;
	}

	/**
	 * Function for finding all combinations of ActionTypes that can be
	 * performed together from the specified Node. The finished products are
	 * stored in the provided Set.
	 * 
	 * @param currentNode
	 *            the Node against whose state all possible ActionTypes are
	 *            checked.
	 * @param simulatedTimeStamp
	 *            the timeStamp in the simulated future in frames.
	 * @param possibleActionTypes
	 *            the Set from which all possible ActionTypes are received. This
	 *            ensures that only possible Actions are being considered for
	 *            the addition to any existing ActionSequences.
	 * @param workingSets
	 *            the Queue from which the initial ActionSequences are received
	 *            and all operations are being performed on. The Queue is empty
	 *            after calling this function.
	 * @param actionSequences
	 *            the Set of all finished ActionSequences that can be performed
	 *            from the current Node. This includes all combinations of
	 *            possible / executable ActionTypes that were provided.
	 * @param maxSequenceCount
	 *            the maximum number of sequences that this function may
	 *            generate. Needed for limiting the output and decreasing the
	 *            complexity.
	 */
	private void findAllPossibleActionTypeCombinations(Node currentNode, int simulatedTimeStamp,
			HashSet<ActionType> possibleActionTypes, Queue<ActionSequence> workingSets,
			HashSet<ActionSequence> actionSequences, int maxSequenceCount) {
		while (!workingSets.isEmpty() && actionSequences.size() < maxSequenceCount) {
			ActionSequence currentActionSequence = workingSets.poll();
			boolean permutationFinished = true;

			// Find all other ActionTypes that are executable in combination
			// with the current sequence. If none are found (permutationFinished
			// = true) then the permutation is final and can not be split /
			// combined any further.
			for (ActionType actionType : possibleActionTypes) {
				// Like above each ActionType may be used only a certain amount
				// of times. Count the number of times the ActionType was
				// already added again and compare that amount with the set
				// threshold.
				int appearanceCount = 0;

				// Count the appearances for the specific ActionType.
				for (ActionType countingActionType : currentActionSequence.getActionTypeSequence()) {
					if (countingActionType == actionType) {
						appearanceCount++;
					}
				}

				// Count below threshold.
				if (appearanceCount < this.maxActionTypesOccurrences.get(actionType)) {
					// Either no further permutations are possible or the
					// maximum size of the sequence is reached.
					permutationFinished = currentActionSequence.getActionTypeSequence()
							.size() >= this.actionSequenceMaxSize
							|| this.tryFinalizingNewActionSequencePermutation(currentActionSequence, actionType,
									currentNode, simulatedTimeStamp, workingSets);
				}
				// Threshold for ActionType reached -> Sequence finished.
				else {
					permutationFinished = true;
				}
			}

			// The sequence could not be modified -> final version was found and
			// is a valid permutation.
			if (permutationFinished) {
				actionSequences.add(currentActionSequence);
			}
		}
	}

	/**
	 * Function for testing if a permutation of ActionTypes already exists in a
	 * provided Queue of sequences ((1, 2) == (2, 1) => {1, 2}). Therefore this
	 * function takes an already existing ActionSequence as well as another
	 * ActionType and combines them. This function also adds the permutation to
	 * the Queue of stored ActionSequences for future testing if the permutation
	 * was not already existent.
	 * 
	 * @param baseActionSequence
	 *            the ActionSequence that is going to be taken as a base.
	 * @param additionalActionType
	 *            the ActionType that is going to be added to the provided
	 *            ActionSequence.
	 * @param currentNode
	 *            the Node that the Sequence is based on (used for checking free
	 *            Units as well as mineral and gas counts).
	 * @param simulatedTimeStamp
	 *            the simulated time stamp of a moment in the future at which
	 *            the ActionType is being executed.
	 * @param actionSequenceStorage
	 *            the storage which
	 *            <ul>
	 *            <li>A) Contains all current permutations of the ActionTypes in
	 *            ActionSequences</li>
	 *            <li>B) Provides the storage for the newly created
	 *            ActionSequence if the permutation provides any new features
	 *            that the existing ones do not.</li>
	 *            </ul>
	 * @return true if the ActionType could not be added and the ActionSequence
	 *         is final and false if the ActionType could be added to the
	 *         ActionSequence.
	 */
	private boolean tryFinalizingNewActionSequencePermutation(ActionSequence baseActionSequence,
			ActionType additionalActionType, Node currentNode, int simulatedTimeStamp,
			Queue<ActionSequence> actionSequenceStorage) {
		// The ActionType's costs as well as the Type that this action
		// relies on must be available.
		int combinedMineralCosts = baseActionSequence.getMineralCost() + additionalActionType.defineMineralCost();
		int combinedGasCosts = baseActionSequence.getGasCost() + additionalActionType.defineGasCost();
		boolean unitsFree = baseActionSequence.getTypesFree().get(additionalActionType.defineRequiredType()) != null
				&& baseActionSequence.getTypesFree().get(additionalActionType.defineRequiredType()) > 0;
		boolean permutationFinal = true;

		if (currentNode.getCurrentMinerals() >= combinedMineralCosts && currentNode.getCurrentGas() >= combinedGasCosts
				&& unitsFree) {
			ActionSequence actionSequence = this.sequenceFactory.receiveSequence();
			boolean actionSequenceCombinationMissing = true;

			// Sequence of ActionTypes being taken.
			actionSequence.setActionTypeSequence(new ArrayList<>(baseActionSequence.getActionTypeSequence()));
			actionSequence.getActionTypeSequence().add(additionalActionType);

			// Count the number of each ActionType in the sequence.
			HashMap<ActionType, Integer> newActionSequenceActionTypeCount = this
					.extractActionTypeAppearances(actionSequence);

			// Do not allow any doubled combinations of ActionTypes:
			// (1, 2) == (2, 1) => {1, 2}
			for (ActionSequence testingActionSequence : actionSequenceStorage) {
				// Count the number of each ActionType in the sequence.
				HashMap<ActionType, Integer> storedActionsActionTypeCount = this
						.extractActionTypeAppearances(testingActionSequence);

				// If no differences are found the sequence is not missing since
				// another combination of ActionTypes provides the same effect
				// and therefore the combination is not needed / is discarded.
				// The newly created ActionSequence must contain at least one
				// different value to be accepted as valid sequence.
				// => One feature that makes the newly created sequence special.
				// The reference ActionSequence can contain keys that the newly
				// created one does not have. But the newly created one MUST
				// have either a key that the reference one does not have or
				// store a difference value for the provided key.
				if (!this.areActionTypeCountsEqual(newActionSequenceActionTypeCount, storedActionsActionTypeCount)) {
					actionSequenceCombinationMissing = false;

					break;
				}
			}

			// When the combination of ActionTypes is not found in any previous
			// combinations, combine all necessary information and add the
			// sequence to the Queue.
			if (actionSequenceCombinationMissing) {
				// Combine the information of the current ActionSequence with
				// the newly calculated values from the added ActionType.
				this.extendActionSequence(actionSequence, baseActionSequence, additionalActionType,
						combinedMineralCosts, combinedGasCosts, simulatedTimeStamp);

				actionSequenceStorage.add(actionSequence);

				// A combination was found. Therefore the permutation is not
				// final.
				permutationFinal = false;
			} else {
				// Free make the sequence instance available again since the
				// combination of actions is already present.
				this.sequenceFactory.markSequenceAsAvailable(actionSequence);
			}
		}

		return permutationFinal;
	}

	/**
	 * Function for counting the appearance of different kinds of ActionTypes in
	 * a ActionSequence.
	 * 
	 * @param actionSequence
	 *            the ActionSequence whose ActionTypes are being counted.
	 * @return a HashMap with the ActionTypes as keys and their appearance count
	 *         as values.
	 */
	private HashMap<ActionType, Integer> extractActionTypeAppearances(ActionSequence actionSequence) {
		HashMap<ActionType, Integer> actionTypeAppearances = new HashMap<>();

		// Count the appearances of each ActionType in the different stored
		// action sequences.
		for (ActionType currentActionType : actionSequence.getActionTypeSequence()) {
			// Add a key in the HashMap if the ActionType was not stored before.
			if (actionTypeAppearances.get(currentActionType) == null) {
				actionTypeAppearances.put(currentActionType, 1);
			} else {
				actionTypeAppearances.put(currentActionType, actionTypeAppearances.get(currentActionType) + 1);
			}
		}

		return actionTypeAppearances;
	}

	/**
	 * Function for testing if the amount of ActionTypes in a newly created
	 * HashMap are the same as another provided reference HashMap. The reference
	 * HashMap can contain more keys than the newly created one can provide.
	 * 
	 * @param newTestHashMap
	 *            the newly created HashMap that will be tested.
	 * @param referenceHashMap
	 *            the already existing HashMap whose stored values the new ones
	 *            are being checked against.
	 * @return true if a difference was found and the newHashMap provides values
	 *         the existing one does not have or false, if the values in the new
	 *         HashMap completely match the stored values in the reference
	 *         HashMap (reference HashMap might also have more / other values
	 *         stored!).
	 */
	private boolean areActionTypeCountsEqual(final HashMap<ActionType, Integer> newTestHashMap,
			final HashMap<ActionType, Integer> referenceHashMap) {
		// Wrapper class needed since the BiConsumer can only access final
		// references outside its declaration and therefore another Object is
		// needed.
		class BooleanWrapper {
			public boolean differenceFound = false;
		}

		final BooleanWrapper booleanWrapper = new BooleanWrapper();

		newTestHashMap.forEach(new BiConsumer<ActionType, Integer>() {

			@Override
			public void accept(ActionType at, Integer i) {
				Integer storedInt = referenceHashMap.get(at);

				// Act if the Integers do not match or nothing was found.
				if (storedInt == null || !storedInt.equals(i)) {
					booleanWrapper.differenceFound = true;
				}
			}
		});

		return booleanWrapper.differenceFound;
	}

	/**
	 * Function for transferring information from one ActionSequence
	 * (transferActionSequence) to another new ActionSequence
	 * (receiverActionSequence) with the addition of a new ActionType that will
	 * be executed together with the already stored ActionTypes in the sequence.
	 * 
	 * @param receiverActionSequence
	 *            the ActionSequence that receives all necessary information.
	 * @param transferActionSequence
	 *            the ActionSequence that provides all necessary information.
	 * @param addedActionType
	 *            the ActionType whose requirements will be added towards the
	 *            occupied types of Units and removed from the free ones.
	 * @param combinedMineralCosts
	 *            the combined mineral costs of the base ActionTypes in the
	 *            ActionSequence and the newly added ActionType.
	 * @param combinedGasCosts
	 *            the combined gas costs of the base ActionTypes in the
	 *            ActionSequence and the newly added ActionType.
	 * @param simulatedTimeStamp
	 *            the time stamp at which the simulated ActionType takes place.
	 */
	private void extendActionSequence(final ActionSequence receiverActionSequence,
			ActionSequence transferActionSequence, ActionType addedActionType, int combinedMineralCosts,
			int combinedGasCosts, int simulatedTimeStamp) {
		// Costs and Type requirements of these Actions.
		receiverActionSequence.setMineralCost(combinedMineralCosts);
		receiverActionSequence.setGasCost(combinedGasCosts);

		// Completion time and free Units.
		// Move the references of each Pair in the ArrayLists of the current
		// action sequence to a new List. This is necessary since copying the
		// Lists directly would cause the new sequences to use the same Lists as
		// storage.
		// => Problem!
		transferActionSequence.getOccupiedTypeTimes()
				.forEach(new BiConsumer<TypeWrapper, ArrayList<Pair<TypeWrapper, Integer>>>() {

					@Override
					public void accept(TypeWrapper typeWrapper, ArrayList<Pair<TypeWrapper, Integer>> list) {
						// Create a new List if necessary.
						if (receiverActionSequence.getOccupiedTypeTimes().get(typeWrapper) == null) {
							receiverActionSequence.getOccupiedTypeTimes().put(typeWrapper,
									new ArrayList<Pair<TypeWrapper, Integer>>());
						}

						ArrayList<Pair<TypeWrapper, Integer>> occupations = receiverActionSequence
								.getOccupiedTypeTimes().get(typeWrapper);

						// Copy the reference for each Pair into the newly
						// created List.
						for (Pair<TypeWrapper, Integer> pair : list) {
							occupations.add(pair);
						}
					}
				});

		// Create a new List if necessary.
		if (receiverActionSequence.getOccupiedTypeTimes().get(addedActionType.defineRequiredType()) == null) {
			receiverActionSequence.getOccupiedTypeTimes().put(addedActionType.defineRequiredType(),
					new ArrayList<Pair<TypeWrapper, Integer>>());
		}
		receiverActionSequence.getOccupiedTypeTimes().get(addedActionType.defineRequiredType()).add(new Pair<>(
				addedActionType.defineResultType(), simulatedTimeStamp + addedActionType.defineCompletionTime()));
		receiverActionSequence.setTypesFree(new HashMap<>(transferActionSequence.getTypesFree()));
		receiverActionSequence.getTypesFree().put(addedActionType.defineRequiredType(),
				receiverActionSequence.getTypesFree().get(addedActionType.defineRequiredType()) - 1);
	}

	// ------------------------------ Getter / Setter

	public void setActionTypes(HashSet<ActionType> actionTypes) {
		this.actionTypes = actionTypes;
	}

}
