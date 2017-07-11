package buildingOrderModule.simulator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeSet;
import java.util.function.BiConsumer;

import bwapi.Pair;
import bwapi.UnitType;

// TODO: UML ADD
/**
 * Simulator.java --- A Simulator for simulating the future possibilities of
 * provided actions with a collection of available / free Units and busy /
 * occupied ones.
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

	// TODO: Needed Change: Return ActionSequences not Node since super Nodes are being worked on again.
	// TODO: Possible Change: Make generic for further use.
	/**
	 * Function for performing a simulation based on available ActionTypes and
	 * provided resources (minerals and gas) as well as a record of available
	 * and occupied / busy Units. This simulation generates a tree with each
	 * layer representing an iteration / certain step in the time line with a
	 * predefined step size. The number of possible Nodes each Node of the
	 * previous layer can produce depends on the number of available ActionTypes
	 * as well as the available / free Units since the Actions require a free
	 * type of Unit to be able to be executed and therefore taken in
	 * consideration. This simulation is quite expensive since it takes various
	 * factors like the availability of certain Units and the different
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
	 * @param unitsFree
	 *            the currently available Units in form of a HashMap: Key =
	 *            Type, Integer = Amount of free Units.
	 * @param unitsWorking
	 *            the currently occupied Units in form of a HashMap: Key = Type,
	 *            ArrayList = Pairs of resulting UnitTypes and the time stamp at
	 *            which they are finished.
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
	 * @return the best resulting Node of the tree with the highest score and
	 *         therefore the best sequence of Actions.
	 */
	public Node simulate(int currentFrameTimeStamp, int frameStep, int stepAmount, int currentMinerals, int currentGas,
			HashMap<UnitType, Integer> unitsFree, HashMap<UnitType, ArrayList<Pair<UnitType, Integer>>> unitsWorking,
			UnitType workerType, int idleScorePenalty, int consecutiveActionsBonus, boolean allowIdle) {
		// Create the root and save it.
		Node root = this.createRoot(currentMinerals, currentGas, currentFrameTimeStamp, unitsFree, unitsWorking);
		this.currentLayerNodes.add(root);
		this.nodes.add(root);

		// Each iteration a new layer is formed. Each layer simulates a state in
		// the future:
		// => Current frame count + i * amount of frames being skipped.
		for (int i = 0; i < stepAmount; i++) {
			final int simulatedFrameTimeStamp = currentFrameTimeStamp + i * frameStep;

			// Collection for the new branch layer on top of the current one. A
			// TreeSet is being used since it provides a sorted insertion which
			// in combination with not allowing the insertion of the same
			// element twice is a lot faster than a simple List.
			// -> Massive performance improvement!
			TreeSet<Node> newLayerNodes = new TreeSet<Node>();

			// Simulated resource gathering:
			int simulatedMineralGain = this.generateSimulatedMineralGain(unitsFree, unitsWorking, workerType,
					frameStep);
			int simulatedGasGain = (int) (frameStep * this.gasPerFrame);

			// Free all Units whose ActionType timeStamp lie in the past.
			if (i != 0) {
				this.tryFreeingFinishedActions(this.currentLayerNodes, simulatedFrameTimeStamp);
			}

			// Iterate through all current branch Nodes and create another
			// branch layer.
			for (Node node : this.currentLayerNodes) {
				HashSet<ActionSequence> actionTypeSequences = this.generateAllPossibleActionTypeSequences(node,
						simulatedFrameTimeStamp);

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
					this.sequenceFactory.markNodeAsAvailable(sequence);
				}
			}

			// Carry over to the newly created layer of Nodes.
			this.nodes.addAll(newLayerNodes);
			this.currentLayerNodes = newLayerNodes;
		}

		
		
		
		
		
		
		// TODO: WIP REMOVE
		System.out.println("Total Node count: " + this.currentLayerNodes.size());
		System.out.println("Nodes with highest Score:");
		System.out.println(" - " + this.currentLayerNodes.first() + " " + this.currentLayerNodes.first().getScore()
				+ " Resources: " + this.currentLayerNodes.first().getCurrentMinerals());
		System.out.println("Simulated best building order:");
		ArrayList<Node> branch = new ArrayList<>();
		Node currentLeafNode = this.currentLayerNodes.first();
		while (currentLeafNode != null) {
			branch.add(0, currentLeafNode);
			currentLeafNode = currentLeafNode.getPreviousNode();
		}
		boolean isRoot = true;
		for (Node node : branch) {
			System.out.println(" Node Score: " + node.getScore());

			if (!node.getChosenActions().isEmpty()) {
				System.out.println(" Sequence start:");

				for (ActionType actionType : node.getChosenActions()) {
					System.out.println("  - " + actionType.getClass().getSimpleName());
				}
			} else {
				if (isRoot) {
					System.out.println(" Root");
					isRoot = false;
				} else {
					System.out.println(" Sequence empty");
				}
			}
		}
		
		
		
		
		
		
		
		
		// Return nearly all Nodes to the NodeFactory.
		Node bestNode = this.currentLayerNodes.first();
		this.currentLayerNodes.clear();

		// Return the Node with the highest score. The Node is not transformed
		// since the timeStamps that mark the start and end of the ActionTypes
		// must be preserved or the starter of the simulation does only know
		// that a certain Action must be taken, but not WHEN!
		return bestNode;
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
	 * @param unitsFree
	 *            the currently available Units that can perform actions.
	 * @param unitsWorking
	 *            the currently unavailable Units that are busy working.
	 * @return a Node that the Simulator can use as root for a generated tree.
	 */
	private Node createRoot(int currentMinerals, int currentGas, int currentFrameTimeStamp,
			HashMap<UnitType, Integer> unitsFree, HashMap<UnitType, ArrayList<Pair<UnitType, Integer>>> unitsWorking) {
		// Create the root of the tree.
		Node root = this.nodeFactory.receiveNode();

		root.setCurrentMinerals(currentMinerals);
		root.setCurrentGas(currentGas);
		root.setFrameTimeStamp(currentFrameTimeStamp);
		root.setUnitsFree(unitsFree);
		root.setUnitsWorking(unitsWorking);

		return root;
	}

	/**
	 * Function for generating the resource values the working Units are going
	 * to produce in the simulated time.
	 * 
	 * @param unitsFree
	 *            the currently available Units.
	 * @param unitsWorking
	 *            the currently unavailable / busy Units.
	 * @param workerType
	 *            the type that represents the worker in the simulation and
	 *            therefore gathers resources.
	 * @param frameStep
	 *            the amount of frames the Simulator takes at one time.
	 * @return the amount of minerals the specified worker Units are gathering.
	 */
	private int generateSimulatedMineralGain(HashMap<UnitType, Integer> unitsFree,
			HashMap<UnitType, ArrayList<Pair<UnitType, Integer>>> unitsWorking, UnitType workerType, int frameStep) {
		// Count all workers. This is not completely accurate since workers can
		// not collect
		int totalWorkerCount = 0;
		if (unitsFree.get(workerType) != null) {
			totalWorkerCount += unitsFree.get(workerType);
		}
		if (unitsWorking.get(workerType) != null) {
			totalWorkerCount += unitsWorking.get(workerType).size();
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
			// Free any finished simulated Units from their work if the
			// timeStamp of their actions lies in the past.
			ArrayList<Pair<UnitType, Integer>> unitsToFree = this.findFinishedSimulatedUnits(node,
					simulatedFrameTimeStamp);

			// Free the simulated Units from their task and add the newly
			// created ones to the List of available Units.
			// Pair.first = UnitType that creates something.
			// Pair.second = TimeStamp of the thing created.
			for (Pair<UnitType, Integer> pair : unitsToFree) {
				Pair<UnitType, Integer> pairToRemove = null;

				// Find the Pair with the specified timeStamp in all the
				// possible Pairs.
				for (Pair<UnitType, Integer> possiblePair : node.getUnitsWorking().get(pair.first)) {
					if (possiblePair.second.equals(pair.second)) {
						pairToRemove = possiblePair;

						break;
					}
				}

				// If the Pair was found, remove it from the ongoing Actions.
				if (pairToRemove != null) {
					node.getUnitsFree().put(pair.first, node.getUnitsFree().get(pair.first) + 1);
					node.getUnitsWorking().get(pair.first).remove(pairToRemove);
					
					
					
					
					if(pair.first == UnitType.Terran_Command_Center) {
						System.out.println("Timestamp finished: " + pair.second);
					}
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
	 * @return a ArrayList of Pairs. These Pairs contain the finished types of
	 *         Units and the corresponding time stamps. Latter is required for
	 *         identification. </br>
	 *         </br>
	 *         Pair.first = UnitType that creates something.</br>
	 *         Pair.second = TimeStamp of the thing created.
	 */
	private ArrayList<Pair<UnitType, Integer>> findFinishedSimulatedUnits(final Node node,
			final int simulatedFrameTimeStamp) {
		// Pair.first = UnitType that creates something.
		// Pair.second = TimeStamp of the thing created.
		final ArrayList<Pair<UnitType, Integer>> unitsToFree = new ArrayList<>();

		node.getUnitsWorking().forEach(new BiConsumer<UnitType, ArrayList<Pair<UnitType, Integer>>>() {

			@Override
			public void accept(UnitType unitType, ArrayList<Pair<UnitType, Integer>> list) {
				for (Pair<UnitType, Integer> pair : list) {
					// Extract all UnitTypes whose actions are finished.
					if (pair.second <= simulatedFrameTimeStamp) {
						unitsToFree.add(new Pair<>(unitType, pair.second));

						// Add the finished UnitType to the other available
						// UnitTypes.
						if (node.getUnitsFree().get(pair.first) == null) {
							node.getUnitsFree().put(pair.first, 0);
						} else {
							node.getUnitsFree().put(pair.first, node.getUnitsFree().get(pair.first) + 1);
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

		// Transfer the occupied and free Units to the new Node.
		// TODO: WIP ALSO DO A DEEP CLONE
		newNode.setUnitsWorking(new HashMap<>(parentNode.getUnitsWorking()));
		newNode.setUnitsFree(new HashMap<>(parentNode.getUnitsFree()));

		return newNode;
	}

	// TODO: Needed Change: Multiple equal Actions must be penalized.
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

		// Transfer the occupied and free Units to the new Node.
		newNode.setUnitsWorking(new HashMap<>(sequence.getOccupiedUnitTimes()));
		newNode.setUnitsFree(new HashMap<>(sequence.getUnitsFree()));

		return newNode;
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
	 * @return a Set of all possible permutations that are the result of the
	 *         ActionTypes being taken based on the provided Node.
	 */
	private HashSet<ActionSequence> generateAllPossibleActionTypeSequences(Node currentNode, int simulatedTimeStamp) {
		HashSet<ActionSequence> actionSequences = new HashSet<>();
		HashSet<ActionType> possibleActionTypes = new HashSet<>();
		Queue<ActionSequence> workingSets = new LinkedList<>();

		// Find the ActionTypes that are actually executable with the current
		// state of the Node and add them to the Queue of ActionType sequences
		// that are being processed.
		for (ActionType actionType : this.actionTypes) {
			if (currentNode.getUnitsFree().get(actionType.defineRequiredUnitType()) != null
					&& currentNode.getUnitsFree().get(actionType.defineRequiredUnitType()) > 0
					&& currentNode.getCurrentMinerals() >= actionType.defineMineralCost()
					&& currentNode.getCurrentGas() >= actionType.defineGasCost()) {
				workingSets.add(this.generateNewActionSequence(currentNode, actionType, simulatedTimeStamp));

				// Save the ActionType for later -> Saves time looking up
				// Actions!
				possibleActionTypes.add(actionType);
			}
		}

		// Work on the Queue of ActionType sequences until all executable
		// permutations are found.
		while (!workingSets.isEmpty()) {
			ActionSequence currentActionSequence = workingSets.poll();
			boolean permutationFinished = true;

			// Find all other ActionTypes that are executable in combination
			// with the current sequence. If none are found (permutationFinished
			// = true) then the permutation is final and can not be split /
			// combined any further.
			for (ActionType actionType : possibleActionTypes) {
				permutationFinished = this.tryFinilizingNewActionSequencePermutation(currentActionSequence, actionType,
						currentNode, simulatedTimeStamp, workingSets);
			}

			// The sequence could not be modified -> final version was found and
			// is a valid permutation.
			if (permutationFinished) {
				actionSequences.add(currentActionSequence);
			}
		}

		return actionSequences;
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
		actionSequence.setOccupiedUnits(new HashMap<UnitType, ArrayList<Pair<UnitType, Integer>>>());
		node.getUnitsWorking().forEach(new BiConsumer<UnitType, ArrayList<Pair<UnitType, Integer>>>() {

			@Override
			public void accept(UnitType unitType, ArrayList<Pair<UnitType, Integer>> list) {
				// Create a new List for the UnitType.
				if (actionSequence.getOccupiedUnitTimes().get(unitType) == null) {
					actionSequence.getOccupiedUnitTimes().put(unitType, new ArrayList<Pair<UnitType, Integer>>());
				}

				ArrayList<Pair<UnitType, Integer>> occupiedUnitsList = actionSequence.getOccupiedUnitTimes()
						.get(unitType);

				// Insert each Pair from the given List into the newly created
				// one.
				for (Pair<UnitType, Integer> pair : list) {
					occupiedUnitsList.add(pair);
				}
			}
		});
		if (actionSequence.getOccupiedUnitTimes().get(actionType.defineRequiredUnitType()) == null) {
			actionSequence.getOccupiedUnitTimes().put(actionType.defineRequiredUnitType(),
					new ArrayList<Pair<UnitType, Integer>>());
		}
		actionSequence.getOccupiedUnitTimes().get(actionType.defineRequiredUnitType()).add(
				new Pair<>(actionType.defineResultUnitType(), simulatedTimeStamp + actionType.defineCompletionTime()));
		actionSequence.setUnitsFree(new HashMap<>(node.getUnitsFree()));
		actionSequence.getUnitsFree().put(actionType.defineRequiredUnitType(),
				node.getUnitsFree().get(actionType.defineRequiredUnitType()) - 1);

		return actionSequence;
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
	private boolean tryFinilizingNewActionSequencePermutation(ActionSequence baseActionSequence,
			ActionType additionalActionType, Node currentNode, int simulatedTimeStamp,
			Queue<ActionSequence> actionSequenceStorage) {
		// The ActionType's costs as well as the UnitType that this action
		// relies on must be available.
		int combinedMineralCosts = baseActionSequence.getMineralCost() + additionalActionType.defineMineralCost();
		int combinedGasCosts = baseActionSequence.getGasCost() + additionalActionType.defineGasCost();
		boolean unitsFree = baseActionSequence.getUnitsFree().get(additionalActionType.defineRequiredUnitType()) != null
				&& baseActionSequence.getUnitsFree().get(additionalActionType.defineRequiredUnitType()) > 0;
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
				this.sequenceFactory.markNodeAsAvailable(actionSequence);
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
		// Costs and UnitType requirements of these Actions.
		receiverActionSequence.setMineralCost(combinedMineralCosts);
		receiverActionSequence.setGasCost(combinedGasCosts);

		// Completion time and free Units.
		// Move the references of each Pair in the ArrayLists of the current
		// action sequence to a new List. This is necessary since copying the
		// Lists directly would cause the new sequences to use the same Lists as
		// storage.
		// => Problem!
		transferActionSequence.getOccupiedUnitTimes()
				.forEach(new BiConsumer<UnitType, ArrayList<Pair<UnitType, Integer>>>() {

					@Override
					public void accept(UnitType unitType, ArrayList<Pair<UnitType, Integer>> list) {
						// Create a new List if necessary.
						if (receiverActionSequence.getOccupiedUnitTimes().get(unitType) == null) {
							receiverActionSequence.getOccupiedUnitTimes().put(unitType,
									new ArrayList<Pair<UnitType, Integer>>());
						}

						ArrayList<Pair<UnitType, Integer>> occupations = receiverActionSequence.getOccupiedUnitTimes()
								.get(unitType);

						// Copy the reference for each Pair into the newly
						// created List.
						for (Pair<UnitType, Integer> pair : list) {
							occupations.add(pair);
						}
					}
				});

		// Create a new List if necessary.
		if (receiverActionSequence.getOccupiedUnitTimes().get(addedActionType.defineRequiredUnitType()) == null) {
			receiverActionSequence.getOccupiedUnitTimes().put(addedActionType.defineRequiredUnitType(),
					new ArrayList<Pair<UnitType, Integer>>());
		}
		receiverActionSequence.getOccupiedUnitTimes().get(addedActionType.defineRequiredUnitType()).add(new Pair<>(
				addedActionType.defineResultUnitType(), simulatedTimeStamp + addedActionType.defineCompletionTime()));
		receiverActionSequence.setUnitsFree(new HashMap<>(transferActionSequence.getUnitsFree()));
		receiverActionSequence.getUnitsFree().put(addedActionType.defineRequiredUnitType(),
				receiverActionSequence.getUnitsFree().get(addedActionType.defineRequiredUnitType()) - 1);
	}

	// ------------------------------ Getter / Setter

}
