package buildingOrderModule.scoringDirector;

import buildingOrderModule.buildActionManagers.BuildActionManager;

/**
 * GameStateUnits_Support.java --- A GameState focused on support Units.
 * 
 * @author P H - 16.07.2017
 *
 */
class GameStateUnits_Support extends GameState {

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.defineFixedScoreUnitsSupport();
	}

}
