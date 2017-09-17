package buildingOrderModule.scoringDirector.gameState;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoringDirector;

/**
 * GameStateFocused_Upgrade.java --- A GameState focused on upgrading Units.
 * 
 * @author P H - 16.07.2017
 *
 */
class GameStateFocused_Upgrade extends GameState {

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateUpgradeFocusedScoreGenerator().generateScore(this,
				this.updateFramesPassedScore);
	}

	@Override
	protected int generateDivider(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateUpgradeFocusedScoreGenerator().generateDivider(this,
				this.updateFramesPassedDivider);
	}

}
