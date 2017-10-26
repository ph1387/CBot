package unitControlModule.unitWrappers;

import bwapi.Unit;
import informationStorage.InformationStorage;
import unitControlModule.stateFactories.StateFactory;
import unitControlModule.stateFactories.StateFactoryTerran_Goliath;

/**
 * PlayerUnitTerran_Goliath.java --- Terran_Goliath Class.
 * 
 * @author P H - 22.09.2017
 *
 */
public class PlayerUnitTerran_Goliath extends PlayerUnitTypeRanged {

	public PlayerUnitTerran_Goliath(Unit unit, InformationStorage informationStorage) {
		super(unit, informationStorage);
	}

	// -------------------- Functions

	@Override
	protected StateFactory createFactory() {
		return new StateFactoryTerran_Goliath();
	}

}
