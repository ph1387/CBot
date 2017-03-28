package unitControlModule.unitWrappers;

import bwapi.Unit;
import unitControlModule.stateFactories.StateFactoryDefault;
import unitControlModule.stateFactories.StateFactory;

/**
 * PlayerUnit_Marine.java --- Terran Marine Class. 
 * @author P H - 26.02.2017
 *
 */
public class PlayerUnitTerran_Marine extends PlayerUnit {

	public PlayerUnitTerran_Marine(Unit unit) {
		super(unit);
	}

	// -------------------- Functions

	@Override
	protected StateFactory createFactory() {
		return new StateFactoryDefault();
	}

	@Override
	protected void customUpdate() {
		
	}
}
