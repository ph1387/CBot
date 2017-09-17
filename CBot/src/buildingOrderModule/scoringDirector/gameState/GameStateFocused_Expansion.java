package buildingOrderModule.scoringDirector.gameState;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoringDirector;

/**
 * GameStateFocused_Expansion.java --- A GameState focused on expansion.
 * 
 * @author P H - 16.07.2017
 *
 */
class GameStateFocused_Expansion extends GameState {

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateExpansionFocusedScoreGenerator().generateScore(this,
				this.updateFramesPassedScore);
	}

	@Override
	protected int generateDivider(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateExpansionFocusedScoreGenerator().generateDivider(this,
				this.updateFramesPassedDivider);
	}

}
