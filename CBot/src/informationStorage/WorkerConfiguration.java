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
	// TODO: UML ADD
	private int totalCombatEngineerCount = 0;
	private boolean workerOnceAssignedScouting = false;

	// The mapping of workers to either machine Units to follow or repair them.
	// TODO: UML ADD
	// Key: Worker following another Unit.
	// Value: Unit that is going to be followed by the worker.
	private UnitMapper unitMapperFollow = new UnitMapper();
	// TODO: UML ADD
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
	
	// TODO: UML ADD
	public void incrementCombatEngineerCount() {
		this.totalCombatEngineerCount++;
	}

	// TODO: UML ADD
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

	// TODO: UML ADD
	public int getTotalCombatEngineerCount() {
		return totalCombatEngineerCount;
	}

	public boolean isWorkerOnceAssignedScouting() {
		return workerOnceAssignedScouting;
	}

	public void setWorkerOnceAssignedScouting(boolean workerOnceAssignedScouting) {
		this.workerOnceAssignedScouting = workerOnceAssignedScouting;
	}

	// TODO: UML ADD
	public UnitMapper getUnitMapperFollow() {
		return unitMapperFollow;
	}

	// TODO: UML ADD
	public UnitMapper getUnitMapperRepair() {
		return unitMapperRepair;
	}

}
