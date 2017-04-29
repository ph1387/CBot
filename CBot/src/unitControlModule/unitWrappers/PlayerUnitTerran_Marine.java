package unitControlModule.unitWrappers;

import bwapi.Unit;
import informationStorage.InformationPreserver;
import unitControlModule.stateFactories.StateFactoryDefault;
import unitControlModule.stateFactories.StateFactory;

/**
 * PlayerUnit_Marine.java --- Terran Marine Class. 
 * @author P H - 26.02.2017
 *
 */
public class PlayerUnitTerran_Marine extends PlayerUnit {

	// TODO: UML
	public PlayerUnitTerran_Marine(Unit unit, InformationPreserver informationPreserver) {
		super(unit, informationPreserver);
	}

	// -------------------- Functions

	@Override
	protected StateFactory createFactory() {
		return new StateFactoryDefault();
	}

}
