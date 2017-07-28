package buildingOrderModule.scoringDirector;

import buildingOrderModule.buildActionManagers.BuildActionManager;

/**
 * GameStateUnits_Combat.java --- A GameState focused on training combat Units.
 * 
 * @author P H - 16.07.2017
 *
 */
class GameStateUnits_Combat extends GameState {

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, BuildActionManager manager) {
		double score = scoringDirector.defineDesiredCombatUnitsPercent()
				- (manager.getCurrentGameInformation().getCurrentCombatUnitsPercent()
						- scoringDirector.defineDesiredCombatUnitsPercent());

		return score;
	}

}
