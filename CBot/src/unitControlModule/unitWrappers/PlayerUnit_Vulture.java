package unitControlModule.unitWrappers;

import bwapi.Unit;
import unitControlModule.stateFactories.SimpleStateFactory;
import unitControlModule.stateFactories.StateFactory;
// TODO: UML
/**
 * PlayerUnit_Vulture.java --- Terran Vulture Class.
 * @author P H - 23.03.2017
 *
 */
public class PlayerUnit_Vulture extends PlayerUnit {

	public PlayerUnit_Vulture(Unit unit) {
		super(unit);
		
		this.extraConfidencePixelRangeToClosestUnits = 64;
	}

	// -------------------- Functions

	@Override
	protected StateFactory createFactory() {
		return new SimpleStateFactory();
	}
}
