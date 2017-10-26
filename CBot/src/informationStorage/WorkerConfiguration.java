package informationStorage;

/**
 * WorkerConfiguration.java --- Configuration Class for the worker Units.
 * 
 * @author P H - 28.04.2017
 *
 */
public class WorkerConfiguration {

	// Initial scouting configuration
	private int workerScoutingTrigger = 9;
	private int totalWorkerCount = 0;
	private int totalCombatEngineerCount = 0;
	private boolean workerOnceAssignedScouting = false;

	// The mapping of workers to either machine Units to follow or repair them.
	// Key: Worker following another Unit.
	// Value: Unit that is going to be followed by the worker.
	private UnitMapper unitMapperFollow = new UnitMapper();
	// Key: Worker repairing.
	// Value: Unit / Building that is being repaired.
	private UnitMapper unitMapperRepair = new UnitMapper();

	public WorkerConfiguration() {

	}

	// -------------------- Functions

	public void incrementTotalWorkerCount() {
		this.totalWorkerCount++;
	}

	public void decrementTotalWorkerCount() {
		this.totalWorkerCount--;
	}

	public void incrementCombatEngineerCount() {
		this.totalCombatEngineerCount++;
	}

	public void decrementCombatEngineerCount() {
		this.totalCombatEngineerCount--;
	}

	// ------------------------------ Getter / Setter

	public int getWorkerScoutingTrigger() {
		return workerScoutingTrigger;
	}

	public int getTotalWorkerCount() {
		return totalWorkerCount;
	}

	public int getTotalCombatEngineerCount() {
		return totalCombatEngineerCount;
	}

	public boolean isWorkerOnceAssignedScouting() {
		return workerOnceAssignedScouting;
	}

	public void setWorkerOnceAssignedScouting(boolean workerOnceAssignedScouting) {
		this.workerOnceAssignedScouting = workerOnceAssignedScouting;
	}

	public UnitMapper getUnitMapperFollow() {
		return unitMapperFollow;
	}

	public UnitMapper getUnitMapperRepair() {
		return unitMapperRepair;
	}

}
