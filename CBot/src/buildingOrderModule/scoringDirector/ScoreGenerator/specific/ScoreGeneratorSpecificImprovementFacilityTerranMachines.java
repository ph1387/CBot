package buildingOrderModule.scoringDirector.ScoreGenerator.specific;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGenerator;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget.ScoreGeneratorIncreaseNormal;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget.ScoreGeneratorIncreaseSlow;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget.ScoreGeneratorIncreaseVerySlow;
import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.UnitType;

/**
 * ScoreGeneratorSpecificImprovementFacilityTerranMachines.java --- A
 * {@link ScoreGenerator} applying a target specific rate to the score. This
 * class focuses on Terran improvement facilities.
 * 
 * @author P H - 03.10.2017
 *
 */
public class ScoreGeneratorSpecificImprovementFacilityTerranMachines extends ScoreGeneratorSpecificImprovementFacility {

	private ScoreGenerator scoreGeneratorIncreaseNormal;
	private ScoreGenerator scoreGeneratorIncreaseSlow;
	private ScoreGenerator scoreGeneratorIncreaseVerySlow;

	public ScoreGeneratorSpecificImprovementFacilityTerranMachines(BuildActionManager manager) {
		super(manager);

		this.scoreGeneratorIncreaseNormal = new ScoreGeneratorIncreaseNormal(this.manager);
		this.scoreGeneratorIncreaseSlow = new ScoreGeneratorIncreaseSlow(this.manager);
		this.scoreGeneratorIncreaseVerySlow = new ScoreGeneratorIncreaseVerySlow(this.manager);
	}

	// -------------------- Functions

	@Override
	protected double generateScoreForGameState(GameState gameState, int framesPassed) throws Exception {
		UnitType unitType = this.extractUnitType(gameState);
		double score = 0.;

		switch (unitType.toString()) {
		case "Terran_Academy":
			score = this.scoreGeneratorIncreaseNormal.generateScore(gameState, framesPassed);
			break;
		case "Terran_Engineering_Bay":
			score = this.scoreGeneratorIncreaseVerySlow.generateScore(gameState, framesPassed);
			break;
		case "Terran_Science_Facility":
			score = this.scoreGeneratorIncreaseSlow.generateScore(gameState, framesPassed);
			break;
		case "Terran_Armory":
			score = this.scoreGeneratorIncreaseSlow.generateScore(gameState, framesPassed);
			break;

		default:
			throw new NullPointerException("Specific ScoreGeneration failed for UnitType: " + unitType);
		}

		return score;
	}

	@Override
	protected int generateDividerForGameState(GameState gameState, int framesPassed) throws Exception {
		return 1;
	}

}
