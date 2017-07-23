package buildingOrderModule.scoringDirector;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import core.Core;

// TODO: UML ADD NOT PUBLIC
/**
 * GameStateUnits_Mineral.java --- A GameState focused on mineral requiring
 * Units.
 * 
 * @author P H - 16.07.2017
 *
 */
class GameStateUnits_Mineral extends GameState {

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, BuildActionManager manager) {
		double minerals = Core.getInstance().getPlayer().minerals();
		double gas = Core.getInstance().getPlayer().gas();
		double totalResources = minerals + gas;
		
		// TODO: WIP REMOVE
		System.out.println("GameState Minerals: " + minerals / totalResources);
		
		return minerals / totalResources;
	}

}
