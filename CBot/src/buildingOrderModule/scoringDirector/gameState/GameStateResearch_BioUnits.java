package buildingOrderModule.scoringDirector.gameState;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoringDirector;
import bwapi.TechType;

// TODO: UML ADD
/**
 * GameStateResearch_Bio.java --- A GameState focused on researching
 * {@link TechType}s for bio Units.
 * 
 * @author P H - 03.10.2017
 *
 */
public class GameStateResearch_BioUnits extends GameState {

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateResearchBioScoreGenerator().generateScore(this,
				this.updateFramesPassedScore);
	}

	@Override
	protected int generateDivider(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateResearchBioScoreGenerator().generateDivider(this,
				this.updateFramesPassedDivider);
	}

}
