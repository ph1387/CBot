package buildingOrderModule.scoringDirector.freeTrainingFacilities;

import bwapi.UnitType;
import core.Core;

/**
 * FreeTrainingFacilities_Center.java --- A GameState that defines the Center as
 * it's training facility whose idlers will be counted.
 * 
 * @author P H - 31.08.2017
 *
 */
public class FreeTrainingFacilities_Center extends GameStateUnits_FreeTrainingFacilities {

	// -------------------- Functions

	@Override
	protected UnitType defineFacilityType() {
		return Core.getInstance().getPlayer().getRace().getCenter();
	}

}
