package buildingOrderModule.scoringDirector.ScoreGenerator.specific;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGenerator;
import buildingOrderModule.scoringDirector.ScoreGenerator.fixed.ScoreGeneratorFixed_One;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget.ScoreGeneratorIncreaseNormal;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget.ScoreGeneratorIncreaseSlow;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget.ScoreGeneratorIncreaseVerySlow;
import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.UnitType;

/**
 * ScoreGeneratorSpecificUnitTerranMachines.java --- A {@link ScoreGenerator}
 * applying a target specific rate to the score. This class focuses on Terran
 * machine-{@link UnitType}s.
 * 
 * @author P H - 03.10.2017
 *
 */
public class ScoreGeneratorSpecificUnitTerranMachines extends ScoreGeneratorSpecificUnit {
	
	// TODO: UML ADD
	private ScoreGenerator scoreGeneratorFixedOne;

	private ScoreGenerator scoreGeneratorIncreaseNormal;
	private ScoreGenerator scoreGeneratorIncreaseSlow;
	private ScoreGenerator scoreGeneratorIncreaseVerySlow;

	public ScoreGeneratorSpecificUnitTerranMachines(BuildActionManager manager) {
		super(manager);

		this.scoreGeneratorFixedOne = new ScoreGeneratorFixed_One(this.manager);

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
		case "Terran_SCV":
			score = this.scoreGeneratorFixedOne.generateScore(gameState, framesPassed);
			break;
		case "Terran_Goliath":
			score = this.scoreGeneratorIncreaseVerySlow.generateScore(gameState, framesPassed);
			break;
		case "Terran_Marine":
			score = this.scoreGeneratorIncreaseNormal.generateScore(gameState, framesPassed);
			break;
		case "Terran_Medic":
			score = this.scoreGeneratorIncreaseNormal.generateScore(gameState, framesPassed);
			break;
		case "Terran_Science_Vessel":
			score = this.scoreGeneratorIncreaseVerySlow.generateScore(gameState, framesPassed);
			break;
		case "Terran_Siege_Tank_Tank_Mode":
			score = this.scoreGeneratorIncreaseSlow.generateScore(gameState, framesPassed);
			break;
		case "Terran_Vulture":
			score = this.scoreGeneratorIncreaseNormal.generateScore(gameState, framesPassed);
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
		return 1;
	}

}
