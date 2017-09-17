package buildingOrderModule.scoringDirector.ScoreGenerator;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.gameState.GameState;
import core.Core;

/**
 * ScoreGeneratorGas.java --- A {@link ScoreGenerator} focusing on the gas ratio
 * of the total number of resources.
 * 
 * @author P H - 15.09.2017
 *
 */
public class ScoreGeneratorGas extends ScoreGeneratorDefault {

	public ScoreGeneratorGas(BuildActionManager manager) {
		super(manager);
	}

	// -------------------- Functions

	@Override
	public double generateScore(GameState gameState, int framesPassed) {
		double minerals = Core.getInstance().getPlayer().minerals();
		double gas = Core.getInstance().getPlayer().gas();
		double totalResources = minerals + gas;

		return gas / totalResources;
	}

}
