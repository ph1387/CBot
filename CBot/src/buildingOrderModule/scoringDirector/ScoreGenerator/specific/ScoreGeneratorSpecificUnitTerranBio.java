package buildingOrderModule.scoringDirector.ScoreGenerator.specific;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGenerator;
import buildingOrderModule.scoringDirector.ScoreGenerator.fixed.ScoreGeneratorFixed_Forbid;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget.ScoreGeneratorIncreaseFast;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget.ScoreGeneratorIncreaseNormal;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget.ScoreGeneratorIncreaseVerySlow;
import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.UnitType;

/**
 * ScoreGeneratorSpecificUnitTerranBio.java --- A {@link ScoreGenerator}
 * applying a target specific rate to the score. This class focuses on Terran
 * bio-{@link UnitType}s.
 * 
 * @author P H - 18.11.2017
 *
 */
public class ScoreGeneratorSpecificUnitTerranBio extends ScoreGeneratorSpecificUnit {

	private ScoreGenerator scoreGeneratorFixedForbid;

	private ScoreGenerator scoreGeneratorIncreaseFast;
	private ScoreGenerator scoreGeneratorIncreaseNormal;
	private ScoreGenerator scoreGeneratorIncreaseVerySlow;

	public ScoreGeneratorSpecificUnitTerranBio(BuildActionManager manager) {
		super(manager);

		this.scoreGeneratorFixedForbid = new ScoreGeneratorFixed_Forbid(this.manager);

		this.scoreGeneratorIncreaseFast = new ScoreGeneratorIncreaseFast(this.manager);
		this.scoreGeneratorIncreaseNormal = new ScoreGeneratorIncreaseNormal(this.manager);
		this.scoreGeneratorIncreaseVerySlow = new ScoreGeneratorIncreaseVerySlow(this.manager);
	}

	// -------------------- Functions

	@Override
	protected double generateScoreForGameState(GameState gameState, int framesPassed) throws Exception {
		UnitType unitType = this.extractUnitType(gameState);
		double score = 0.;

		switch (unitType.toString()) {
		case "Terran_Goliath":
			score = this.scoreGeneratorFixedForbid.generateScore(gameState, framesPassed);
			break;
		case "Terran_Marine":
			score = this.scoreGeneratorIncreaseFast.generateScore(gameState, framesPassed);
			break;
		case "Terran_Medic":
			score = this.scoreGeneratorIncreaseFast.generateScore(gameState, framesPassed);
			break;
		case "Terran_Science_Vessel":
			score = this.scoreGeneratorIncreaseVerySlow.generateScore(gameState, framesPassed);
			break;
		case "Terran_Siege_Tank_Tank_Mode":
			score = this.scoreGeneratorIncreaseNormal.generateScore(gameState, framesPassed);
			break;
		case "Terran_Vulture":
			score = this.scoreGeneratorFixedForbid.generateScore(gameState, framesPassed);
			break;
		case "Terran_Wraith":
			score = this.scoreGeneratorIncreaseVerySlow.generateScore(gameState, framesPassed);
			break;

		default:
			throw new NullPointerException("Specific ScoreGeneration failed for UnitType: " + unitType);
		}

		return score;
	}

	@Override
	protected int generateDividerForGameState(GameState gameState, int framesPassed) throws Exception {
		UnitType unitType = this.extractUnitType(gameState);
		int divider = 1;

		if (unitType == UnitType.Terran_Vulture || unitType == UnitType.Terran_Goliath) {
			divider = this.scoreGeneratorFixedForbid.generateDivider(gameState, framesPassed);
		}

		return divider;
	}

}
