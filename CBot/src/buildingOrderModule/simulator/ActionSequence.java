package buildingOrderModule.simulator;

import java.util.ArrayList;
import java.util.HashMap;

import bwapi.Pair;
import bwapi.UnitType;

// TODO: UML ADD
/**
 * ActionSequence.java --- A sequence of {@link ActionType}s that can / will be
 * executed. This class contains extra information like the combined mineral and
 * gas costs of these actions being taken. Also this class contains the
 * information regarding the availability and occupation of certain UnitTypes
 * that are required to perform the stored actions.
 * 
 * @author P H - 06.07.2017
 *
 */
public class ActionSequence {

	private ArrayList<ActionType> actionTypeSequence = new ArrayList<>();
	private int mineralCost = 0;
	private int gasCost = 0;
	// Key: The UnitType of that is being considered.
	// Value: The amount of the UnitType that is free / available to receive
	// orders.
	private HashMap<UnitType, Integer> UnitsFree = new HashMap<>();
	// Key: The creating UnitType.
	// Value: A List of Pairs of UnitTypes that are being created by the key
	// UnitType.
	// ---> Pair: "What" is "When" finished.
	private HashMap<UnitType, ArrayList<Pair<UnitType, Integer>>> occupiedUnitTimes = new HashMap<>();

	public ActionSequence() {

	}

	// -------------------- Functions

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

	public HashMap<UnitType, Integer> getUnitsFree() {
		return UnitsFree;
	}

	public void setUnitsFree(HashMap<UnitType, Integer> unitsFree) {
		UnitsFree = unitsFree;
	}

	public HashMap<UnitType, ArrayList<Pair<UnitType, Integer>>> getOccupiedUnitTimes() {
		return occupiedUnitTimes;
	}

	public void setOccupiedUnits(HashMap<UnitType, ArrayList<Pair<UnitType, Integer>>> occupiedUnits) {
		this.occupiedUnitTimes = occupiedUnits;
	}
}
