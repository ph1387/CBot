package unitControlModule.unitWrappers;

import bwapi.Unit;
import unitControlModule.stateFactories.SimpleStateFactory;
import unitControlModule.stateFactories.StateFactory;

/**
 * PlayerUnit_Marine.java --- Terran Marine Class. 
 * @author P H - 26.02.2017
 *
 */
public class PlayerUnit_Marine extends PlayerUnit {

	public PlayerUnit_Marine(Unit unit) {
		super(unit);
	}

	// -------------------- Functions

	@Override
	protected StateFactory createFactory() {
		return new SimpleStateFactory();
	}
}
