package buildingOrderModule.scoringDirector.ScoreGenerator.fixed;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGenerator;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGeneratorDefault;
import buildingOrderModule.scoringDirector.gameState.GameState;

// TODO: UML PACKAGE
/**
 * ScoreGeneratorFixed.java --- A {@link ScoreGenerator} based on returning a
 * fixed value as score.
 * 
 * @author P H - 15.09.2017
 *
 */
public class ScoreGeneratorFixed extends ScoreGeneratorDefault {

	private double score;
	private int divider;

	public ScoreGeneratorFixed(BuildActionManager manager, double score, int divider) {
		super(manager);

		this.score = score;
		this.divider = divider;
	}

	// -------------------- Functions

	@Override
	public double generateScore(GameState gameState, int framesPassed) {
		return this.score;
	}

	@Override
	public int generateDivider(GameState gameState, int framesPassed) {
		return this.divider;
	}

}
