package buildingOrderModule.scoringDirector;

import buildingOrderModule.buildActionManagers.BuildActionManager;

// TODO: UML ADD NOT PUBLIC
/**
 * GameStateUnits_Bio.java --- A GameState focused on bio Units.
 * 
 * @author P H - 16.07.2017
 *
 */
class GameStateUnits_Bio extends GameState {

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.defineFixedScoreUnitsBio();
	}

}
