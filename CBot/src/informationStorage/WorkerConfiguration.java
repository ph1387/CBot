package informationStorage;

/**
 * WorkerConfiguration.java --- Configuration Class for the worker Units.
 * 
 * @author P H - 28.04.2017
 *
 */
public class WorkerConfiguration {

	// TODO: UML REMOVE
//	private int constructionCounterMax = 20;

	// Initial scouting configuration
	private int workerScoutingTrigger = 9;
	private int totalWorkerCount = 0;
	private boolean workerOnceAssignedScouting = false;
	
	// TODO: UML REMOVE
//	private HashMap<Unit, UnitType> mappedBuildActions = new HashMap<>();

	// TODO: UML REMOVE
//	private Queue<UnitType> buildingQueue = new LinkedList<>();
//	private HashSet<Unit> buildingsBeingCreated = new HashSet<Unit>();

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

	// TODO: UML REMOVE
//	public int getConstructionCounterMax() {
//		return constructionCounterMax;
//	}

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

	// TODO: UML REMOVE
//	public Queue<UnitType> getBuildingQueue() {
//		return buildingQueue;
//	}

	// TODO: UML REMOVE
//	public HashMap<Unit, UnitType> getMappedBuildActions() {
//		return mappedBuildActions;
//	}

	// TODO: UML REMOVE
//	public HashSet<Unit> getBuildingsBeingCreated() {
//		return buildingsBeingCreated;
//	}
}
