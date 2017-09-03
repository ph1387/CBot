package buildingOrderModule.scoringDirector.freeTrainingFacilities;

import bwapi.UnitType;

/**
 * FreeTrainingFacilities_TerranCommandCenter.java --- A GameState that defines
 * the Terran_Command_Center as it's training facility whose idlers will be
 * counted. </br>
 * <b>NOTE:</b></br>
 * The general Center GameState {@link FreeTrainingFacilities_Center} should be
 * used instead of this one, since it provides a generally valid state for
 * workers of each race.
 * 
 * @author P H - 31.08.2017
 *
 */
public class FreeTrainingFacilities_TerranCommandCenter extends GameStateUnits_FreeTrainingFacilities {

	// -------------------- Functions

	@Override
	protected UnitType defineFacilityType() {
		return UnitType.Terran_Command_Center;
	}

}
