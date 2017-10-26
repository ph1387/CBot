package buildingOrderModule.scoringDirector.gameState;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoringDirector;
import bwapi.UpgradeType;

/**
 * GameStateUpgrade_MachineUnits.java --- A GameState focused on performing
 * upgrades ({@link UpgradeType}s) for machine Units.
 * 
 * @author P H - 03.10.2017
 *
 */
public class GameStateUpgrade_MachineUnits extends GameState {

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateUpgradesMachinesScoreGenerator().generateScore(this,
				this.updateFramesPassedScore);
	}

	@Override
	protected int generateDivider(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateUpgradesMachinesScoreGenerator().generateDivider(this,
				this.updateFramesPassedDivider);
	}

}
