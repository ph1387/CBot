package buildingOrderModule.scoringDirector;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import core.Core;

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
	protected double generateScore(ScoringDirector scoringDirector, BuildActionManager manager) {
		double centers = (double) (manager.getCurrentGameInformation().getCurrentUnitCounts()
				.get(Core.getInstance().getPlayer().getRace().getCenter()));
		double workers = (double) (manager.getCurrentGameInformation().getCurrentUnitCounts()
				.get(Core.getInstance().getPlayer().getRace().getWorker()));

		return this.generalMultiplier * (centers / workers);
	}

}
