package buildingOrderModule.scoringDirector.freeTrainingFacilities;

import bwapi.UnitType;

/**
 * FreeTrainingFacilities_TerranBarracks.java --- A GameState that defines the
 * Terran_Barracks as it's training facility whose idlers will be counted.
 * 
 * @author P H - 31.08.2017
 *
 */
public class FreeTrainingFacilities_TerranBarracks extends GameStateUnits_FreeTrainingFacilities {

	// -------------------- Functions

	@Override
	protected UnitType defineFacilityType() {
		return UnitType.Terran_Barracks;
	}

}
