package buildingOrderModule.scoringDirector;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import core.Core;

/**
 * GameStateUnits_Gas.java --- A GameState focused on vaspene gas requiring
 * Units.
 * 
 * @author P H - 16.07.2017
 *
 */
class GameStateUnits_Gas extends GameState {

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, BuildActionManager manager) {
		double minerals = Core.getInstance().getPlayer().minerals();
		double gas = Core.getInstance().getPlayer().gas();
		double totalResources = minerals + gas;
		
		return gas / totalResources;
	}

}
