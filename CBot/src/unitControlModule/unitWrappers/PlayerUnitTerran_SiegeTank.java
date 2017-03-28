package unitControlModule.unitWrappers;

import bwapi.Unit;
import unitControlModule.stateFactories.StateFactoryTerran_SiegeTank;
import unitControlModule.stateFactories.StateFactory;

/**
 * PlayerUnit_Siege_Tank.java --- Terran Siege Tank Class.
 * @author P H - 25.03.2017
 *
 */
public class PlayerUnitTerran_SiegeTank extends PlayerUnit {

	public PlayerUnitTerran_SiegeTank(Unit unit) {
		super(unit);
		
		// TODO: Possible Change: Siege Mode range change
		// Siege mode -> min range = 2 * 32 (+ extra)
		this.extraConfidencePixelRangeToClosestUnits = 128;
		this.confidenceDefault = 0.4;
	}

	// -------------------- Functions
	
	@Override
	protected StateFactory createFactory() {
		return new StateFactoryTerran_SiegeTank();
	}
}
