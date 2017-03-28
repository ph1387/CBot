package unitControlModule.unitWrappers;

import bwapi.Unit;
import unitControlModule.stateFactories.StateFactory;
import unitControlModule.stateFactories.StateFactoryTerran_SCV;

/**
 * PlayerUnitTerran_SCV.java --- Terran SCV Class.
 * @author P H - 25.03.2017
 *
 */
public class PlayerUnitTerran_SCV extends PlayerUnit {

	public PlayerUnitTerran_SCV(Unit unit) {
		super(unit);
	}

	// -------------------- Functions

	@Override
	protected StateFactory createFactory() {
		return new StateFactoryTerran_SCV();
	}

	@Override
	protected void customUpdate() {
		
	}
}
