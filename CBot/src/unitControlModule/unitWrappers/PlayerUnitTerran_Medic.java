package unitControlModule.unitWrappers;

import bwapi.Unit;
import informationStorage.InformationStorage;
import unitControlModule.stateFactories.StateFactory;
import unitControlModule.stateFactories.StateFactoryTerran_Medic;

/**
 * PlayerUnitTerran_Medic.java --- Terran_Medic Class. Used for healing
 * Bio-Units on the map like Terran_Marines and Terran_Firebats.
 * 
 * @author P H - 27.06.2017
 *
 */
public class PlayerUnitTerran_Medic extends PlayerUnit {

	public PlayerUnitTerran_Medic(Unit unit, InformationStorage informationStorage) {
		super(unit, informationStorage);
	}

	// -------------------- Functions

	@Override
	protected StateFactory createFactory() {
		return new StateFactoryTerran_Medic();
	}
}
