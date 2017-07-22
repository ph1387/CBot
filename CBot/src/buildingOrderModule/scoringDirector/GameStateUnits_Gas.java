package buildingOrderModule.scoringDirector;

import core.Core;

// TODO: UML ADD NOT PUBLIC
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
	protected double generateScore(ScoringDirector scoringDirector, GameStateCurrentInformation currenInformation) {
		double minerals = Core.getInstance().getPlayer().minerals();
		double gas = Core.getInstance().getPlayer().gas();
		double totalResources = minerals + gas;
		
		// TODO: WIP REMOVE
		System.out.println("GameState Gas: " + gas / totalResources);
		
		return gas / totalResources;
	}

}
