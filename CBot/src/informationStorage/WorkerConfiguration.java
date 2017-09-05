package informationStorage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import bwapi.Unit;
import bwapi.UnitType;

/**
 * WorkerConfiguration.java --- Configuration Class for the worker Units.
 * 
 * @author P H - 28.04.2017
 *
 */
public class WorkerConfiguration {

	private int constructionCounterMax = 20;

	// Initial scouting configuration
	private int workerScoutingTrigger = 9;
	private int totalWorkerCount = 0;
	private boolean workerOnceAssignedScouting = false;

	private HashMap<Unit, UnitType> mappedBuildActions = new HashMap<>();

	private Queue<UnitType> buildingQueue = new LinkedList<>();
	private HashSet<Unit> buildingsBeingCreated = new HashSet<Unit>();

	public WorkerConfiguration() {

	}

	// -------------------- Functions

	public void incrementTotalWorkerCount() {
		this.totalWorkerCount++;
	}

	public void decrementTotalWorkerCount() {
		this.totalWorkerCount--;
	}

	// ------------------------------ Getter / Setter

	public int getConstructionCounterMax() {
		return constructionCounterMax;
	}

	public int getWorkerScoutingTrigger() {
		return workerScoutingTrigger;
	}

	public int getTotalWorkerCount() {
		return totalWorkerCount;
	}

	public boolean isWorkerOnceAssignedScouting() {
		return workerOnceAssignedScouting;
	}

	public void setWorkerOnceAssignedScouting(boolean workerOnceAssignedScouting) {
		this.workerOnceAssignedScouting = workerOnceAssignedScouting;
	}

	public Queue<UnitType> getBuildingQueue() {
		return buildingQueue;
	}

	public HashMap<Unit, UnitType> getMappedBuildActions() {
		return mappedBuildActions;
	}

	public HashSet<Unit> getBuildingsBeingCreated() {
		return buildingsBeingCreated;
	}
}
