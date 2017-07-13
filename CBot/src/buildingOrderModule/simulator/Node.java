package buildingOrderModule.simulator;

import java.util.ArrayList;
import java.util.HashMap;

import bwapi.Pair;

// TODO: UML ADD
/**
 * Node.java --- Class that is being used to represent a single Element in the
 * tree that is being generated by the Simulator.
 * 
 * @author P H - 05.07.2017
 *
 */

public class Node implements Comparable<Node> {

	private HashMap<TypeWrapper, Integer> typesFree = new HashMap<>();
	private HashMap<TypeWrapper, ArrayList<Pair<TypeWrapper, Integer>>> typesWorking = new HashMap<>();
	private HashMap<ActionType, Integer> actionTypeOccurrences = new HashMap<>();
	private ArrayList<ActionType> chosenActions = new ArrayList<>();
	private int currentMinerals = 0;
	private int currentGas = 0;
	private int frameTimeStamp = -1;
	private Node previousNode = null;
	private int score = 0;

	// Influence of minerals and gas on the score:
	private static double influenceMinerals = 0.9;
	private static double influenceGas = 0.9;

	public Node() {

	}

	// -------------------- Functions

	/**
	 * Function for summing up the score of all ActionTypes that this Node is
	 * holding.
	 * 
	 * @return the sum of all the scores of all ActionTypes the Node is
	 *         currently storing.
	 */
	public int generateScoreOfActions() {
		int totalScore = 0;

		for (ActionType actionType : this.chosenActions) {
			totalScore += actionType.defineScore();
		}
		return totalScore;
	}

	@Override
	public int compareTo(Node node) {
		int totalScoreNodeOne = generateSingleNodeScore(this);
		int totalScoreNodeTwo = generateSingleNodeScore(node);

		// Highest score = index 0.
		if (totalScoreNodeTwo > totalScoreNodeOne) {
			return 1;
		} else if (totalScoreNodeTwo < totalScoreNodeOne) {
			return -1;
		} else {
			return 0;
		}
	}

	private static int generateSingleNodeScore(Node node) {
		// Take the stored influences and apply them to the mineral and gas
		// counts.
		return node.getScore()
				+ (int) (node.getCurrentMinerals() * influenceMinerals + node.getCurrentGas() * influenceGas);
	}

	// ------------------------------ Getter / Setter

	public HashMap<TypeWrapper, Integer> getTypesFree() {
		return typesFree;
	}

	public void setTypesFree(HashMap<TypeWrapper, Integer> unitsFree) {
		this.typesFree = unitsFree;
	}

	public HashMap<TypeWrapper, ArrayList<Pair<TypeWrapper, Integer>>> getTypesWorking() {
		return typesWorking;
	}

	public void setTypesWorking(HashMap<TypeWrapper, ArrayList<Pair<TypeWrapper, Integer>>> unitsWorking) {
		this.typesWorking = unitsWorking;
	}
	
	public HashMap<ActionType, Integer> getActionTypeOccurrences() {
		return actionTypeOccurrences;
	}

	public void setActionTypeOccurrences(HashMap<ActionType, Integer> actionTypeOccurrences) {
		this.actionTypeOccurrences = actionTypeOccurrences;
	}

	public ArrayList<ActionType> getChosenActions() {
		return chosenActions;
	}

	public void setChosenActions(ArrayList<ActionType> chosenActions) {
		this.chosenActions = chosenActions;
	}

	public int getCurrentMinerals() {
		return currentMinerals;
	}

	public void setCurrentMinerals(int currentMinerals) {
		this.currentMinerals = currentMinerals;
	}

	public int getCurrentGas() {
		return currentGas;
	}

	public void setCurrentGas(int currentGas) {
		this.currentGas = currentGas;
	}

	public int getFrameTimeStamp() {
		return frameTimeStamp;
	}

	public void setFrameTimeStamp(int frameTimeStamp) {
		this.frameTimeStamp = frameTimeStamp;
	}

	public Node getPreviousNode() {
		return previousNode;
	}

	public void setPreviousNode(Node previousNode) {
		this.previousNode = previousNode;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public double getInfluenceMinerals() {
		return influenceMinerals;
	}

	public void setInfluenceMinerals(double influenceMinerals) {
		Node.influenceMinerals = influenceMinerals;
	}

	public double getInfluenceGas() {
		return influenceGas;
	}

	public void setInfluenceGas(double influenceGas) {
		Node.influenceGas = influenceGas;
	}
}
