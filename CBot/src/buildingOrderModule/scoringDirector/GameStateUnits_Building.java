package buildingOrderModule.scoringDirector;

import buildingOrderModule.buildActionManagers.BuildActionManager;

/**
 * GameStateUnits_Building.java --- A GameState focused on buildings.
 * 
 * @author P H - 16.07.2017
 *
 */
class GameStateUnits_Building extends GameState {

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, BuildActionManager manager) {
		double score = scoringDirector.defineDesiredBuildingsPercent()
				- (manager.getCurrentGameInformation().getCurrentBuildingsPercent()
						- scoringDirector.defineDesiredBuildingsPercent());
		return score;
	}

}
