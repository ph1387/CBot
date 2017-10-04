package buildingOrderModule.scoringDirector.ScoreGenerator;

import java.util.HashSet;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.gameState.GameState;
import buildingOrderModule.scoringDirector.gameState.GameStateUnits_TrainingFacilitiesIdle;
import bwapi.Unit;
import bwapi.UnitType;

/**
 * ScoreGeneratorTrainingFacilitiesIdle.java --- A {@link ScoreGenerator}
 * focused on generating a divider based on the number of idling training
 * facilities. The type of facility is determined by the given
 * {@link GameState}. The more free facilities the higher the divider.
 * 
 * @author P H - 15.09.2017
 *
 */
public class ScoreGeneratorTrainingFacilitiesIdle extends ScoreGeneratorDefault {

	private static double Score = 1.;

	public ScoreGeneratorTrainingFacilitiesIdle(BuildActionManager manager) {
		super(manager);
	}

	// -------------------- Functions

	@Override
	public double generateScore(GameState gameState, int framesPassed) {
		return Score;
	}

	@Override
	public int generateDivider(GameState gameState, int framesPassed) {
		int divider = 0;

		try {
			UnitType facilityType = ((GameStateUnits_TrainingFacilitiesIdle) gameState).getFacilityType();
			HashSet<Unit> facilities = manager.getInformationStorage().getCurrentGameInformation().getCurrentUnits()
					.getOrDefault(facilityType, new HashSet<Unit>());

			// Count the idling facilities.
			for (Unit facility : facilities) {
				if (!facility.isTraining()) {
					divider++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return divider;
	}
}
