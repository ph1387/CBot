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
	// TODO: UML REMOVE
	// private int totalWorkerCount = 0;
	private int totalCombatEngineerCount = 0;
	private boolean workerOnceAssignedScouting = false;

	// The mapping of workers to either machine Units to follow or repair them.
	// Key: Worker following another Unit.
	// Value: Unit that is going to be followed by the worker.
	private UnitMapper unitMapperFollow = new UnitMapper();
	// Key: Worker repairing.
	// Value: Unit / Building that is being repaired.
	private UnitMapper unitMapperRepair = new UnitMapper();
	// Key: Worker attacking
	// Value: (Enemy) Unit
	private UnitMapper unitMapperAttack = new UnitMapper();

	public WorkerConfiguration() {

	}

	// -------------------- Functions

	// TODO: UML REMOVE
	// public void incrementTotalWorkerCount() {
	// TODO: UML REMOVE
	// public void decrementTotalWorkerCount() {

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

	// TODO: UML REMOVE
	// public int getTotalWorkerCount() {

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

	public UnitMapper getUnitMapperAttack() {
		return unitMapperAttack;
	}

}
