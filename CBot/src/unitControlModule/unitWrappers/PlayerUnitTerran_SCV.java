package unitControlModule.unitWrappers;

import bwapi.Unit;
import informationStorage.InformationStorage;
import unitControlModule.stateFactories.StateFactory;
import unitControlModule.stateFactories.StateFactoryTerran_SCV;

/**
 * PlayerUnitTerran_SCV.java --- Terran SCV Class.
 * 
 * @author P H - 25.03.2017
 *
 */
public class PlayerUnitTerran_SCV extends PlayerUnitWorker {

	public PlayerUnitTerran_SCV(Unit unit, InformationStorage informationStorage) {
		super(unit, informationStorage);
	}

	// -------------------- Functions

	@Override
	protected StateFactory createFactory() {
		return new StateFactoryTerran_SCV();
	}
}
