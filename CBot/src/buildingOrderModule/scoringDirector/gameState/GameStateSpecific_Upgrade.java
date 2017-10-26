package buildingOrderModule.scoringDirector.gameState;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoringDirector;
import bwapi.UpgradeType;

/**
 * GameStateSpecific_Upgrade.java --- A GameState focused on performing specific
 * upgrades ({@link UpgradeType}s).
 * 
 * @author P H - 03.10.2017
 *
 */
public class GameStateSpecific_Upgrade extends GameState {

	private UpgradeType specificUpgradeType;

	public GameStateSpecific_Upgrade(UpgradeType upgradeType) {
		this.specificUpgradeType = upgradeType;
	}

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateSpecificUpgradeScoreGenerator().generateScore(this,
				this.updateFramesPassedScore);
	}

	@Override
	protected int generateDivider(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateSpecificUpgradeScoreGenerator().generateDivider(this,
				this.updateFramesPassedDivider);
	}

	// ------------------------------ Getter / Setter

	public UpgradeType getSpecificUpgradeType() {
		return specificUpgradeType;
	}

}
