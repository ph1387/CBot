package unitControlModule.unitWrappers;

import bwapi.Unit;
import informationStorage.InformationStorage;
import unitControlModule.stateFactories.StateFactory;
import unitControlModule.stateFactories.StateFactoryTerran_Marine;

/**
 * PlayerUnit_Marine.java --- Terran_Marine Class.
 * 
 * @author P H - 26.02.2017
 *
 */
public class PlayerUnitTerran_Marine extends PlayerUnitTypeRanged {

	public PlayerUnitTerran_Marine(Unit unit, InformationStorage informationStorage) {
		super(unit, informationStorage);
	}

	// -------------------- Functions

	@Override
	protected StateFactory createFactory() {
		return new StateFactoryTerran_Marine();
	}

}
