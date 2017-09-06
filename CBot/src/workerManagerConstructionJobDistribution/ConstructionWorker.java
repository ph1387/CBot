package workerManagerConstructionJobDistribution;

import bwapi.Unit;

/**
 * ConstructionWorker.java --- Interface all workers that are used in the
 * {@link WorkerManagerConstructionJobDistribution} instances must implement.
 * 
 * @author P H - 05.09.2017
 *
 */
public interface ConstructionWorker {

	/**
	 * Function for retrieving the Unit that will be constructing the building.
	 * 
	 * @return the Unit that will construct the building.
	 */
	public Unit getUnit();

}
