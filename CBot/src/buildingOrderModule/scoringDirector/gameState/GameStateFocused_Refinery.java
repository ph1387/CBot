package buildingOrderModule.scoringDirector.gameState;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoringDirector;

/**
 * GameStateFocused_Refinery.java --- A GameState focused on the construction of
 * refineries since they are required at newly acquired base locations for
 * gathering gas.
 * 
 * @author P H - 23.07.2017
 *
 */
class GameStateFocused_Refinery extends GameState {

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateRefineryScoreGenerator().generateScore(this,
				this.updateFramesPassedScore);
	}

	@Override
	protected int generateDivider(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateRefineryScoreGenerator().generateDivider(this,
				this.updateFramesPassedDivider);
	}

}
