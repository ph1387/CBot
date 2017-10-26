package buildingOrderModule.scoringDirector.gameState;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoringDirector;
import bwapi.UpgradeType;

/**
 * GameStateUpgrade_BioUnits.java --- A GameState focused on performing upgrades
 * ({@link UpgradeType}s) for bio Units.
 * 
 * @author P H - 03.10.2017
 *
 */
public class GameStateUpgrade_BioUnits extends GameState {

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateUpgradesBioScoreGenerator().generateScore(this,
				this.updateFramesPassedScore);
	}

	@Override
	protected int generateDivider(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateUpgradesBioScoreGenerator().generateDivider(this,
				this.updateFramesPassedDivider);
	}

}
