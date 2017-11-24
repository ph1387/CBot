package buildingOrderModule.scoringDirector.ScoreGenerator.specific;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGenerator;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget.ScoreGeneratorIncreaseNormal;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget.ScoreGeneratorIncreaseSlow;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget.ScoreGeneratorIncreaseVerySlow;
import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.UnitType;

//TODO: UML ADD
/**
 * ScoreGeneratorSpecificImprovementFacilityTerranBio.java --- A
 * {@link ScoreGenerator} applying a target specific rate to the score. This
 * class focuses on Terran bio-Unit improvement facilities.
 * 
 * @author P H - 18.11.2017
 *
 */
public class ScoreGeneratorSpecificImprovementFacilityTerranBio extends ScoreGeneratorSpecificImprovementFacility {

	private ScoreGenerator scoreGeneratorIncreaseNormal;
	private ScoreGenerator scoreGeneratorIncreaseSlow;
	private ScoreGenerator scoreGeneratorIncreaseVerySlow;

	public ScoreGeneratorSpecificImprovementFacilityTerranBio(BuildActionManager manager) {
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
			score = this.scoreGeneratorIncreaseSlow.generateScore(gameState, framesPassed);
			break;
		case "Terran_Science_Facility":
			score = this.scoreGeneratorIncreaseNormal.generateScore(gameState, framesPassed);
			break;
		case "Terran_Armory":
			score = this.scoreGeneratorIncreaseVerySlow.generateScore(gameState, framesPassed);
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
