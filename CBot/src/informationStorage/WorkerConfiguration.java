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
	private boolean workerOnceAssignedScouting = false;

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

}
