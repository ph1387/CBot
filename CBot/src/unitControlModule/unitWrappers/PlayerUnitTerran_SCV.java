package unitControlModule.unitWrappers;

import bwapi.Unit;
import informationStorage.InformationStorage;
import unitControlModule.stateFactories.StateFactory;
import unitControlModule.stateFactories.StateFactoryTerran_SCV;
import workerManagerConstructionJobDistribution.WorkerManagerConstructionJobDistribution;
import workerManagerResourceSpotAllocation.WorkerManagerResourceSpotAllocation;

/**
 * PlayerUnitTerran_SCV.java --- Terran SCV Class.
 * 
 * @author P H - 25.03.2017
 *
 */
public class PlayerUnitTerran_SCV extends PlayerUnitWorker {

	public PlayerUnitTerran_SCV(Unit unit, InformationStorage informationStorage,
			WorkerManagerResourceSpotAllocation workerManagerResourceSpotAllocation,
			WorkerManagerConstructionJobDistribution workerManagerConstructionJobDistribution) {
		super(unit, informationStorage, workerManagerResourceSpotAllocation, workerManagerConstructionJobDistribution);
	}

	// -------------------- Functions

	@Override
	protected StateFactory createFactory() {
		return new StateFactoryTerran_SCV();
	}
}
