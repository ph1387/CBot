package buildingOrderModule.scoringDirector.ScoreGenerator;

import java.util.HashSet;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.gameState.GameState;
import buildingOrderModule.scoringDirector.gameState.GameStateUnits_TrainingFacilitiesFree;
import bwapi.Unit;
import bwapi.UnitType;

/**
 * ScoreGeneratorTrainingFacilitiesFree.java --- A {@link ScoreGenerator}
 * focused on generating a score based on the number of training facilities at
 * which a Unit can currently be trained. The type of facility is determined by
 * the given {@link GameState}. The more free facilities the higher the score.
 * 
 * @author P H - 15.09.2017
 *
 */
public class ScoreGeneratorTrainingFacilitiesFree extends ScoreGeneratorDefault {

	private static int Divider = 1;

	public ScoreGeneratorTrainingFacilitiesFree(BuildActionManager manager) {
		super(manager);
	}

	// -------------------- Functions

	@Override
	public double generateScore(GameState gameState, int framesPassed) {
		double score = 0.;

		try {
			UnitType facilityType = ((GameStateUnits_TrainingFacilitiesFree) gameState).getFacilityType();
			HashSet<Unit> facilities = manager.getInformationStorage().getCurrentGameInformation().getCurrentUnits()
					.getOrDefault(facilityType, new HashSet<Unit>());

			// Count the idling facilities.
			for (Unit facility : facilities) {
				if (!facility.isTraining()) {
					score++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return score;
	}

	@Override
	public int generateDivider(GameState gameState, int framesPassed) {
		return Divider;
	}
}
