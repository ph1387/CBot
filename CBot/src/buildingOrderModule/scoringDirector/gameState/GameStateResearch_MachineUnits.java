package buildingOrderModule.scoringDirector.gameState;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoringDirector;
import bwapi.TechType;

//TODO: UML ADD
/**
 * GameStateResearch_Machines.java --- A GameState focused on researching
 * {@link TechType}s for machine Units.
 * 
 * @author P H - 03.10.2017
 *
 */
public class GameStateResearch_MachineUnits extends GameState {

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateResearchMachinesScoreGenerator().generateScore(this,
				this.updateFramesPassedScore);
	}

	@Override
	protected int generateDivider(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateResearchMachinesScoreGenerator().generateDivider(this,
				this.updateFramesPassedDivider);
	}

}
