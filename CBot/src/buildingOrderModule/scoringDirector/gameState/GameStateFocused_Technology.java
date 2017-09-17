package buildingOrderModule.scoringDirector.gameState;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoringDirector;

/**
 * GameStateFocused_Technology.java --- A GameState focused on researching
 * technologies.
 * 
 * @author P H - 16.07.2017
 *
 */
class GameStateFocused_Technology extends GameState {

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateTechnologyFocusedScoreGenerator().generateScore(this,
				this.updateFramesPassedScore);
	}

	@Override
	protected int generateDivider(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateTechnologyFocusedScoreGenerator()
				.generateDivider(this, this.updateFramesPassedDivider);
	}

}
