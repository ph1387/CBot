package buildingOrderModule.scoringDirector.ScoreGenerator.specific;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGenerator;
import buildingOrderModule.scoringDirector.ScoreGenerator.fixed.ScoreGeneratorFixed_Forbid;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget.ScoreGeneratorIncreaseNormal;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget.ScoreGeneratorIncreaseVerySlow;
import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.UpgradeType;

/**
 * ScoreGeneratorSpecificUpgradeTerranBio.java --- A {@link ScoreGenerator}
 * applying a target specific rate to the score. This class focuses on Terran
 * bio-{@link UpgradeType}s.
 * 
 * @author P H - 18.11.2017
 *
 */
public class ScoreGeneratorSpecificUpgradeTerranBio extends ScoreGeneratorSpecificUpgrade {

	private ScoreGenerator scoreGeneratorFixedForbid;

	private ScoreGenerator scoreGeneratorIncreaseNormal;
	private ScoreGenerator scoreGeneratorIncreaseVerySlow;

	public ScoreGeneratorSpecificUpgradeTerranBio(BuildActionManager manager) {
		super(manager);

		this.scoreGeneratorFixedForbid = new ScoreGeneratorFixed_Forbid(this.manager);

		this.scoreGeneratorIncreaseNormal = new ScoreGeneratorIncreaseNormal(this.manager);
		this.scoreGeneratorIncreaseVerySlow = new ScoreGeneratorIncreaseVerySlow(this.manager);
	}

	// -------------------- Functions

	@Override
	protected double generateScoreForGameState(GameState gameState, int framesPassed) throws Exception {
		UpgradeType upgradeType = this.extractUpgradeType(gameState);
		double score = 0.;

		switch (upgradeType.toString()) {
		case "Terran_Infantry_Armor":
			score = scoreGeneratorIncreaseVerySlow.generateScore(gameState, framesPassed);
			break;
		case "Terran_Infantry_Weapons":
			score = scoreGeneratorIncreaseVerySlow.generateScore(gameState, framesPassed);
			break;
		case "Terran_Vehicle_Plating":
			score = this.scoreGeneratorFixedForbid.generateScore(gameState, framesPassed);
			break;
		case "Terran_Vehicle_Weapons":
			score = this.scoreGeneratorFixedForbid.generateScore(gameState, framesPassed);
			break;
		case "Ion_Thrusters":
			score = this.scoreGeneratorFixedForbid.generateScore(gameState, framesPassed);
			break;
		case "Charon_Boosters":
			score = this.scoreGeneratorFixedForbid.generateScore(gameState, framesPassed);
			break;
		case "U_238_Shells":
			score = scoreGeneratorIncreaseNormal.generateScore(gameState, framesPassed);
			break;

		default:
			throw new NullPointerException("Specific ScoreGeneration failed for UpgradeType: " + upgradeType);
		}

		return score;
	}

	@Override
	protected int generateDividerForGameState(GameState gameState, int framesPassed) throws Exception {
		UpgradeType upgradeType = this.extractUpgradeType(gameState);
		int divider = 1;

		if (upgradeType == UpgradeType.Terran_Vehicle_Plating || upgradeType == UpgradeType.Terran_Vehicle_Weapons
				|| upgradeType == UpgradeType.Ion_Thrusters || upgradeType == UpgradeType.Charon_Boosters) {
			divider = this.scoreGeneratorFixedForbid.generateDivider(gameState, framesPassed);
		}

		return divider;
	}

}
