package unitControlModule.unitWrappers;

import bwapi.Unit;
import unitControlModule.InformationPreserver;
import unitControlModule.stateFactories.StateFactory;
import unitControlModule.stateFactories.StateFactoryTerran_SCV;

/**
 * PlayerUnitTerran_SCV.java --- Terran SCV Class.
 * 
 * @author P H - 25.03.2017
 *
 */
public class PlayerUnitTerran_SCV extends PlayerUnitWorker {

	// TODO: UML
	public PlayerUnitTerran_SCV(Unit unit, InformationPreserver informationPreserver) {
		super(unit, informationPreserver);
	}

	// -------------------- Functions

	@Override
	protected StateFactory createFactory() {
		return new StateFactoryTerran_SCV();
	}

	@Override
	protected void customUpdate() {
		super.customUpdate();
	}
}
