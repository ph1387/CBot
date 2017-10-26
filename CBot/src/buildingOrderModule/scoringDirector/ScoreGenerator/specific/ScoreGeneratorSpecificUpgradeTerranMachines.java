package buildingOrderModule.scoringDirector.ScoreGenerator.specific;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGenerator;
import buildingOrderModule.scoringDirector.ScoreGenerator.fixed.ScoreGeneratorFixed_Forbid;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget.ScoreGeneratorIncreaseNormal;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget.ScoreGeneratorIncreaseSlow;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget.ScoreGeneratorIncreaseVerySlow;
import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.UpgradeType;

/**
 * ScoreGeneratorSpecificUpgradeTerranMachines.java --- A {@link ScoreGenerator}
 * applying a target specific rate to the score. This class focuses on Terran
 * {@link UpgradeType}s.
 * 
 * @author P H - 03.10.2017
 *
 */
public class ScoreGeneratorSpecificUpgradeTerranMachines extends ScoreGeneratorSpecificUpgrade {

	private ScoreGenerator scoreGeneratorFixedForbid;

	private ScoreGenerator scoreGeneratorIncreaseNormal;
	private ScoreGenerator scoreGeneratorIncreaseSlow;
	private ScoreGenerator scoreGeneratorIncreaseVerySlow;

	public ScoreGeneratorSpecificUpgradeTerranMachines(BuildActionManager manager) {
		super(manager);

		this.scoreGeneratorFixedForbid = new ScoreGeneratorFixed_Forbid(this.manager);

		this.scoreGeneratorIncreaseNormal = new ScoreGeneratorIncreaseNormal(this.manager);
		this.scoreGeneratorIncreaseSlow = new ScoreGeneratorIncreaseSlow(this.manager);
		this.scoreGeneratorIncreaseVerySlow = new ScoreGeneratorIncreaseVerySlow(this.manager);
	}

	// -------------------- Functions

	@Override
	protected double generateScoreForGameState(GameState gameState, int framesPassed) throws Exception {
		UpgradeType upgradeType = this.extractUpgradeType(gameState);
		double score = 0.;

		switch (upgradeType.toString()) {
		case "Terran_Infantry_Armor":
			score = scoreGeneratorFixedForbid.generateScore(gameState, framesPassed);
			break;
		case "Terran_Infantry_Weapons":
			score = scoreGeneratorFixedForbid.generateScore(gameState, framesPassed);
			break;
		case "Terran_Vehicle_Plating":
			score = this.scoreGeneratorIncreaseVerySlow.generateScore(gameState, framesPassed);
			break;
		case "Terran_Vehicle_Weapons":
			score = this.scoreGeneratorIncreaseVerySlow.generateScore(gameState, framesPassed);
			break;
		case "Ion_Thrusters":
			score = this.scoreGeneratorIncreaseNormal.generateScore(gameState, framesPassed);
			break;
		case "Charon_Boosters":
			score = this.scoreGeneratorIncreaseSlow.generateScore(gameState, framesPassed);
			break;
		case "U_238_Shells":
			score = scoreGeneratorFixedForbid.generateScore(gameState, framesPassed);
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

		if (upgradeType == UpgradeType.Terran_Infantry_Armor || upgradeType == UpgradeType.Terran_Infantry_Weapons
				|| upgradeType == UpgradeType.U_238_Shells) {
			divider = this.scoreGeneratorFixedForbid.generateDivider(gameState, framesPassed);
		}

		return divider;
	}

}
