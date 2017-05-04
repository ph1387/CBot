package unitControlModule.unitWrappers;

import bwapi.Unit;
import informationStorage.InformationStorage;
import unitControlModule.stateFactories.StateFactoryDefault;
import unitControlModule.stateFactories.StateFactory;

/**
 * PlayerUnit_Marine.java --- Terran Marine Class. 
 * @author P H - 26.02.2017
 *
 */
public class PlayerUnitTerran_Marine extends PlayerUnit {

	public PlayerUnitTerran_Marine(Unit unit, InformationStorage informationStorage) {
		super(unit, informationStorage);
	}

	// -------------------- Functions

	@Override
	protected StateFactory createFactory() {
		return new StateFactoryDefault();
	}

}
