package buildingOrderModule.scoringDirector.gameState;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoringDirector;
import bwapi.TechType;

//TODO: UML ADD
/**
 * GameStateResearch_FlyingUnits.java --- A GameState focused on researching
 * {@link TechType}s for flying Units.
 * 
 * @author P H - 03.10.2017
 *
 */
public class GameStateResearch_FlyingUnits extends GameState {

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateResearchFlyingScoreGenerator().generateScore(this,
				this.updateFramesPassedScore);
	}

	@Override
	protected int generateDivider(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateResearchFlyingScoreGenerator().generateDivider(this,
				this.updateFramesPassedDivider);
	}

}
