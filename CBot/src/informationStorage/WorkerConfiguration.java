package informationStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import bwapi.Unit;
import bwapi.UnitType;

// TODO: UML
/**
 * WorkerConfiguration.java --- Configuration Class for the worker Units.
 * 
 * @author P H - 28.04.2017
 *
 */
public class WorkerConfiguration {

	private int maxNumberMining = 2;
	private int maxNumberGatheringGas = 0; // TODO: 3
	private int pixelGatherSearchRadius = 350;
	private int constructionCounterMax = 20;
	
	// Initial scouting configuration
	private int workerScoutingTrigger = 9;
	private int totalWorkerCount = 0;
	// TODO: UML
	private boolean workerOnceAssignedScouting = false;

	// Mapped: gathering sources (Units) -> Units (worker)
	// Each gathering source holds the Units that are currently working on it.
	private HashMap<Unit, ArrayList<Unit>> mappedAccessibleGatheringSources = new HashMap<Unit, ArrayList<Unit>>();
	// Used to prevent double mapping of the same gathering source in one cycle.
	private HashMap<Unit, ArrayList<Unit>> mappedSourceContenders = new HashMap<Unit, ArrayList<Unit>>();
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

	public int getMaxNumberMining() {
		return maxNumberMining;
	}

	public int getMaxNumberGatheringGas() {
		return maxNumberGatheringGas;
	}

	public int getPixelGatherSearchRadius() {
		return pixelGatherSearchRadius;
	}

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

	public HashMap<Unit, ArrayList<Unit>> getMappedAccessibleGatheringSources() {
		return mappedAccessibleGatheringSources;
	}

	public HashMap<Unit, ArrayList<Unit>> getMappedSourceContenders() {
		return mappedSourceContenders;
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
