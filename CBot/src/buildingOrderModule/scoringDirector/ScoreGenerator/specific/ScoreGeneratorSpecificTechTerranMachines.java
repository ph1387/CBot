package buildingOrderModule.scoringDirector.ScoreGenerator.specific;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGenerator;
import buildingOrderModule.scoringDirector.ScoreGenerator.fixed.ScoreGeneratorFixed_Forbid;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget.ScoreGeneratorIncreaseNormal;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget.ScoreGeneratorIncreaseVeryFast;
import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.TechType;

// TODO: UML ADD
/**
 * ScoreGeneratorSpecificTechTerranMachines.java --- A {@link ScoreGenerator}
 * applying a target specific rate to the score. This class focuses on Terran
 * {@link TechType}s.
 * 
 * @author P H - 03.10.2017
 *
 */
public class ScoreGeneratorSpecificTechTerranMachines extends ScoreGeneratorSpecificTech {

	private ScoreGenerator scoreGeneratorFixedForbid;

	private ScoreGenerator scoreGeneratorIncreaseVeryFast;
	private ScoreGenerator scoreGeneratorIncreaseNormal;

	public ScoreGeneratorSpecificTechTerranMachines(BuildActionManager manager) {
		super(manager);

		this.scoreGeneratorFixedForbid = new ScoreGeneratorFixed_Forbid(this.manager);

		this.scoreGeneratorIncreaseVeryFast = new ScoreGeneratorIncreaseVeryFast(this.manager);
		this.scoreGeneratorIncreaseNormal = new ScoreGeneratorIncreaseNormal(this.manager);
	}

	// -------------------- Functions

	@Override
	protected double generateScoreForGameState(GameState gameState, int framesPassed) throws Exception {
		TechType techType = this.extractTechType(gameState);
		double score = 0.;

		switch (techType.toString()) {
		case "Stim_Packs":
			score = this.scoreGeneratorFixedForbid.generateScore(gameState, framesPassed);
			break;
		case "Tank_Siege_Mode":
			score = this.scoreGeneratorIncreaseNormal.generateScore(gameState, framesPassed);
			break;
		case "Spider_Mines":
			score = this.scoreGeneratorIncreaseVeryFast.generateScore(gameState, framesPassed);
			break;

		default:
			throw new NullPointerException("Specific ScoreGeneration failed for TechType: " + techType);
		}

		return score;
	}

	@Override
	protected int generateDividerForGameState(GameState gameState, int framesPassed) throws Exception {
		TechType techType = this.extractTechType(gameState);
		int divider = 1;

		if (techType == TechType.Stim_Packs) {
			divider = this.scoreGeneratorFixedForbid.generateDivider(gameState, framesPassed);
		}

		return divider;
	}

}
