package buildingOrderModule.scoringDirector.gameState;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoringDirector;

/**
 * GameStateUnits_Machines.java --- A GameState focused on machine Units.
 * 
 * @author P H - 15.09.2017
 *
 */
public class GameStateUnits_Machines extends GameState {

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateMachineScoreGenerator().generateScore(this,
				this.updateFramesPassedScore);
	}

	@Override
	protected int generateDivider(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateMachineScoreGenerator().generateDivider(this,
				this.updateFramesPassedDivider);
	}

}
