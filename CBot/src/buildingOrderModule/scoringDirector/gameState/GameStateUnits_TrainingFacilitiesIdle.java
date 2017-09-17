package buildingOrderModule.scoringDirector.gameState;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoringDirector;
import bwapi.UnitType;

/**
 * GameStateUnits_TrainingFacilitiesIdle.java --- A GameState focused on
 * providing a score based on the available training facilities that are
 * currently idling for the specific UnitType. The more facilities idle the
 * <b>lower</b> the score!
 * 
 * @author P H - 15.09.2017
 *
 */
public class GameStateUnits_TrainingFacilitiesIdle extends GameState {

	private UnitType facilityType;

	public GameStateUnits_TrainingFacilitiesIdle(UnitType facilityType) {
		this.facilityType = facilityType;
	}

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateIdleTrainingFacilityScoreGenerator()
				.generateScore(this, this.updateFramesPassedScore);
	}

	@Override
	protected int generateDivider(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateIdleTrainingFacilityScoreGenerator()
				.generateDivider(this, this.updateFramesPassedDivider);
	}

	// ------------------------------ Getter / Setter

	public UnitType getFacilityType() {
		return facilityType;
	}

}
