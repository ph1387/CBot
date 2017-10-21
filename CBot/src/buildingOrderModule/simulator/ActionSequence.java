package buildingOrderModule.simulator;

import java.util.ArrayList;
import java.util.HashMap;

import bwapi.Pair;

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
public class ActionSequence {

	private ArrayList<ActionType> actionTypeSequence = new ArrayList<>();
	private int mineralCost = 0;
	private int gasCost = 0;
	// Key: The UnitType of that is being considered.
	// Value: The amount of the Type that is free / available to receive
	// orders.
	private HashMap<TypeWrapper, Integer> typesFree = new HashMap<>();
	// Key: The creating Type.
	// Value: A List of Pairs of Types that are being created by the key
	// Type.
	// ---> Pair: "What" is "When" finished.
	private HashMap<TypeWrapper, ArrayList<Pair<TypeWrapper, Integer>>> occupiedTypeTimes = new HashMap<>();

	public ActionSequence() {

	}

	// -------------------- Functions

	// TODO: UML ADD
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

	// ------------------------------ Getter / Setter

	public ArrayList<ActionType> getActionTypeSequence() {
		return actionTypeSequence;
	}

	public void setActionTypeSequence(ArrayList<ActionType> sequence) {
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

	public HashMap<TypeWrapper, ArrayList<Pair<TypeWrapper, Integer>>> getOccupiedTypeTimes() {
		return occupiedTypeTimes;
	}

	public void setOccupiedTypeTimes(HashMap<TypeWrapper, ArrayList<Pair<TypeWrapper, Integer>>> occupiedUnits) {
		this.occupiedTypeTimes = occupiedUnits;
	}
}
