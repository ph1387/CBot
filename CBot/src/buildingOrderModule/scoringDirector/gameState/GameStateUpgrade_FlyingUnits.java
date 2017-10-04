package buildingOrderModule.scoringDirector.gameState;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoringDirector;
import bwapi.UpgradeType;

//TODO: UML ADD
/**
 * GameStateUpgrade_FlyingUnits.java --- A GameState focused on performing
 * upgrades ({@link UpgradeType}s) for flying Units.
 * 
 * @author P H - 03.10.2017
 *
 */
public class GameStateUpgrade_FlyingUnits extends GameState {

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateUpgradesFlyingScoreGenerator().generateScore(this,
				this.updateFramesPassedScore);
	}

	@Override
	protected int generateDivider(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateUpgradesFlyingScoreGenerator().generateDivider(this,
				this.updateFramesPassedDivider);
	}

}
