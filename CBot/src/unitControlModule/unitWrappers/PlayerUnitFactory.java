package unitControlModule.unitWrappers;

import bwapi.Unit;
import informationStorage.InformationPreserver;

/**
 * PlayerUnitFactory.java --- Factory used to create different kinds of
 * PlayerUnits.
 * 
 * @author P H - 26.02.2017
 *
 */
public class PlayerUnitFactory {
	
	// -------------------- Functions
	
	// TODO: UML
	public static PlayerUnit createSiegeTank(Unit unit, InformationPreserver informationPreserver) {
		return new PlayerUnitTerran_SiegeTank(unit, informationPreserver);
	}
	
	// TODO: UML
	public static PlayerUnit createMarine(Unit unit, InformationPreserver informationPreserver) {
		return new PlayerUnitTerran_Marine(unit, informationPreserver);
	}
	
	// TODO: UML
	public static PlayerUnit createVulture(Unit unit, InformationPreserver informationPreserver) {
		return new PlayerUnitTerran_Vulture(unit, informationPreserver);
	}
	
	// TODO: UML
	public static PlayerUnit createSCV(Unit unit, InformationPreserver informationPreserver) {
		return new PlayerUnitTerran_SCV(unit, informationPreserver);
	}
}
