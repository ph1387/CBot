package buildingOrderModule.scoringDirector.ScoreGenerator;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.gameState.GameState;
import core.Core;

/**
 * ScoreGeneratorWorker.java --- A {@link ScoreGenerator} focusing on the
 * generation of scores for workers. This Class uses a general multiplier for
 * ensuring that multiple workers in relation to the centers are trained.
 * 
 * @author P H - 15.09.2017
 *
 */
public class ScoreGeneratorWorker extends ScoreGeneratorDefault {

	private static int Divider = 1;

	// TODO: UML CHANGE 8
	// Multiplier needed for ensuring that enough workers per center are
	// trained. the higher the number the more workers are trained.
	private double generalMultiplier = 16.;

	public ScoreGeneratorWorker(BuildActionManager manager) {
		super(manager);
	}

	// -------------------- Functions

	@Override
	public double generateScore(GameState gameState, int framesPassed) {
		double centers = (double) (this.manager.getCurrentGameInformation().getCurrentUnitCounts()
				.get(Core.getInstance().getPlayer().getRace().getCenter()));
		double workers = (double) (this.manager.getCurrentGameInformation().getCurrentUnitCounts()
				.get(Core.getInstance().getPlayer().getRace().getWorker()));

		return this.generalMultiplier * (centers / workers);
	}

	@Override
	public int generateDivider(GameState gameState, int framesPassed) {
		return Divider;
	}

}
