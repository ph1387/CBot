package buildingOrderModule.simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bwapi.Pair;

// TODO: UML ADD INTERFACE
/**
 * ActionSequence.java --- A sequence of {@link ActionType}s that can / will be
 * executed. This class contains extra information like the combined mineral and
 * gas costs of these actions being taken. Also this class contains the
 * information regarding the availability and occupation of certain Types that
 * are required to perform the stored actions.
 * 
 * @author P H - 06.07.2017
 *
 */
public class ActionSequence implements Comparable<ActionSequence> {

	// TODO: UML CHANGE TYPE
	private List<ActionType> actionTypeSequence = new ArrayList<>();
	private int mineralCost = 0;
	private int gasCost = 0;
	// Key: The UnitType of that is being considered.
	// Value: The amount of the Type that is free / available to receive
	// orders.
	private HashMap<TypeWrapper, Integer> typesFree = new HashMap<>();
	// TODO: UML CHANGE TYPE
	// Key: The creating Type.
	// Value: A List of Pairs of Types that are being created by the key
	// Type.
	// ---> Pair: "What" is "When" finished.
	private HashMap<TypeWrapper, List<Pair<TypeWrapper, Integer>>> occupiedTypeTimes = new HashMap<>();

	public ActionSequence() {

	}

	// -------------------- Functions

	/**
	 * Function for resetting all values stored in the instance.
	 */
	public void reset() {
		this.actionTypeSequence = new ArrayList<>();
		this.mineralCost = 0;
		this.gasCost = 0;
		this.typesFree = new HashMap<>();
		this.occupiedTypeTimes = new HashMap<>();
	}

	// TODO: WIP
	// TODO: UML ADD
	/**
	 * Function for summing up the score of all ActionTypes that this instance
	 * is referencing.
	 * 
	 * @return the sum of all the scores of all ActionTypes the instance is
	 *         currently storing.
	 */
	public int generateScoreOfActions() {
		int totalScore = 0;

		for (ActionType actionType : this.actionTypeSequence) {
			totalScore += actionType.defineScore();
		}
		return totalScore;
	}

	// TODO: UML ADD
	@Override
	public int compareTo(ActionSequence actionSequence) {
		int totalScoreActionSequenceOne = this.generateScoreOfActions();
		int totalScoreActionSequenceTwo = actionSequence.generateScoreOfActions();

		// Highest score = index 0.
		// => Sorted: Descending.
		return -1 * (Integer.compare(totalScoreActionSequenceOne, totalScoreActionSequenceTwo));
	}

	// ------------------------------ Getter / Setter

	// TODO: UML CHANGE TYPE
	public List<ActionType> getActionTypeSequence() {
		return actionTypeSequence;
	}

	// TODO: UML CHANGE PARAMS
	public void setActionTypeSequence(List<ActionType> sequence) {
		this.actionTypeSequence = sequence;
	}

	public int getMineralCost() {
		return mineralCost;
	}

	public void setMineralCost(int mineralCost) {
		this.mineralCost = mineralCost;
	}

	public int getGasCost() {
		return gasCost;
	}

	public void setGasCost(int gasCost) {
		this.gasCost = gasCost;
	}

	public HashMap<TypeWrapper, Integer> getTypesFree() {
		return typesFree;
	}

	public void setTypesFree(HashMap<TypeWrapper, Integer> unitsFree) {
		typesFree = unitsFree;
	}

	// TODO: UML CHANGE TYPE
	public HashMap<TypeWrapper, List<Pair<TypeWrapper, Integer>>> getOccupiedTypeTimes() {
		return occupiedTypeTimes;
	}

	// TODO: UML CHANGE PARAMS
	public void setOccupiedTypeTimes(HashMap<TypeWrapper, List<Pair<TypeWrapper, Integer>>> occupiedUnits) {
		this.occupiedTypeTimes = occupiedUnits;
	}
}
