package unitControlModule.unitWrappers;

import bwapi.Unit;
import informationStorage.InformationStorage;
import unitControlModule.stateFactories.StateFactory;
import unitControlModule.stateFactories.StateFactoryTerran_Wraith;

// TODO: UML ADD
/**
 * PlayerUnitTerran_Wraith.java --- Terran_Wraith Class.
 * 
 * @author P H - 13.09.2017
 *
 */
public class PlayerUnitTerran_Wraith extends PlayerUnitTypeFlying {

	public PlayerUnitTerran_Wraith(Unit unit, InformationStorage informationStorage) {
		super(unit, informationStorage);
	}

	// -------------------- Functions

	@Override
	protected StateFactory createFactory() {
		return new StateFactoryTerran_Wraith();
	}

}
