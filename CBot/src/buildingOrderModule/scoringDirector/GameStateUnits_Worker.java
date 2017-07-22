package buildingOrderModule.scoringDirector;

import core.Core;

// TODO: UML ADD NOT PUBLIC
/**
 * GameStateUnits_Worker.java --- A GameState focused on training worker Units.
 * 
 * @author P H - 16.07.2017
 *
 */
class GameStateUnits_Worker extends GameState {

	private double generalMultiplier = 4.;

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, GameStateCurrentInformation currenInformation) {
		double centers = (double) (currenInformation.getCurrentUnits().get(Core.getInstance().getPlayer().getRace().getCenter()));
		double workers = (double) (currenInformation.getCurrentUnits().get(Core.getInstance().getPlayer().getRace().getWorker()));

		// TODO: WIP REMOVE
		System.out.println("GameState WorkerUnits: " + (this.generalMultiplier * (centers / workers)));
		
		return this.generalMultiplier * (centers / workers);
	}

}
