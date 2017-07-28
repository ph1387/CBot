package buildingOrderModule.scoringDirector;

import buildingOrderModule.buildActionManagers.BuildActionManager;

/**
 * GameStateUnits_Healer.java --- A GameState focused on healing Units.
 * 
 * @author P H - 16.07.2017
 *
 */
class GameStateUnits_Healer extends GameState {

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.defineFixedScoreUnitsHealer();
	}

}
