package buildingOrderModule.scoringDirector.freeTrainingFacilities;

import bwapi.UnitType;

// TODO: UML ADD
/**
 * FreeTrainingFacilities_TerranStarport.java --- A GameState that defines the
 * Terran_Starport as it's training facility whose idlers will be counted.
 * 
 * @author P H - 31.08.2017
 *
 */
public class FreeTrainingFacilities_TerranStarport extends GameStateUnits_FreeTrainingFacilities {

	// -------------------- Functions

	@Override
	protected UnitType defineFacilityType() {
		return UnitType.Terran_Starport;
	}

}
