package buildingOrderModule.scoringDirector.freeTrainingFacilities;

import java.util.HashSet;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.GameState;
import buildingOrderModule.scoringDirector.ScoringDirector;
import bwapi.Unit;
import bwapi.UnitType;

// TODO: UML ADD
/**
 * GameStateUnits_FreeTrainingFacilities.java --- A GameState focused on
 * providing a score based on the available training facilities that are
 * currently idling for the specific UnitType.
 * 
 * @author P H - 31.08.2017
 *
 */
public abstract class GameStateUnits_FreeTrainingFacilities extends GameState {

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, BuildActionManager manager) {
		HashSet<Unit> facilities = manager.getInformationStorage().getCurrentGameInformation().getCurrentUnits()
				.get(this.defineFacilityType());
		int idlingFacilities = 0;

		// Count the idling facilities.
		if (facilities != null) {
			for (Unit facility : facilities) {
				if (!facility.isTraining()) {
					idlingFacilities++;
				}
			}
		}

		return idlingFacilities;
	}

	// TODO: UML ADD
	@Override
	protected int defineDivider() {
		// The score represents the number of idling facilities. Therefore
		// casting it to int represents the needed divider that will be used in
		// generating the multiplier for the score.
		return (int) this.getCurrentScore();
	}

	/**
	 * Function for defining the UnitType of the factory whose idlers will be
	 * counted.
	 * 
	 * @return the UnitType of the factories whose idlers will be counted.
	 */
	protected abstract UnitType defineFacilityType();

}
