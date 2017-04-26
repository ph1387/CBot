package unitControlModule.unitWrappers;

import bwapi.Unit;

/**
 * PlayerUnitFactory.java --- Factory used to create different kinds of
 * PlayerUnits.
 * 
 * @author P H - 26.02.2017
 *
 */
public class PlayerUnitFactory {
	
	// -------------------- Functions
	
	public static PlayerUnit createSiegeTank(Unit unit) {
		return new PlayerUnitTerran_SiegeTank(unit);
	}
	
	public static PlayerUnit createMarine(Unit unit) {
		return new PlayerUnitTerran_Marine(unit);
	}
	
	public static PlayerUnit createVulture(Unit unit) {
		return new PlayerUnitTerran_Vulture(unit);
	}
	
	public static PlayerUnit createSCV(Unit unit) {
		return new PlayerUnitTerran_SCV(unit);
	}
}
