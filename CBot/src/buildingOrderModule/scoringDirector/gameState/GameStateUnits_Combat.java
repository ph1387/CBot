package buildingOrderModule.scoringDirector.gameState;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoringDirector;

/**
 * GameStateUnits_Combat.java --- A GameState focused on training combat Units.
 * 
 * @author P H - 16.07.2017
 *
 */
class GameStateUnits_Combat extends GameState {

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateCombatScoreGenerator().generateScore(this,
				this.updateFramesPassedScore);
	}

	@Override
	protected int generateDivider(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateCombatScoreGenerator().generateDivider(this,
				this.updateFramesPassedDivider);
	}

}
