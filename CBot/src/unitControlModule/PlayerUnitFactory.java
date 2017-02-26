package unitControlModule;

import bwapi.Unit;
import unitControlModule.unitWrappers.PlayerUnit;
import unitControlModule.unitWrappers.PlayerUnit_Marine;

/**
 * PlayerUnitFactory.java --- Factory used to create different kinds of
 * PlayerUnits.
 * 
 * @author P H - 26.02.2017
 *
 */
public class PlayerUnitFactory {

	// -------------------- Functions

	public static PlayerUnit createMarine(Unit unit) {
		return new PlayerUnit_Marine(unit);
	}
}
