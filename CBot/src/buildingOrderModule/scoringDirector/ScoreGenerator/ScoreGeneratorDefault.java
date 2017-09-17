package buildingOrderModule.scoringDirector.ScoreGenerator;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.gameState.GameState;

/**
 * ScoreGeneratorDefault.java --- A default {@link ScoreGenerator} only
 * implementing the needed, basic divider. Also stores the reference to the
 * {@link BuildActionManager} that is associated with the generation of the
 * scores.
 * 
 * @author P H - 15.09.2017
 *
 */
public abstract class ScoreGeneratorDefault implements ScoreGenerator {

	private static final int DEFAULT_DIVIDER = 1;

	protected BuildActionManager manager;

	public ScoreGeneratorDefault(BuildActionManager manager) {
		this.manager = manager;
	}

	// -------------------- Functions

	@Override
	public int generateDivider(GameState gameState, int framesPassed) {
		return DEFAULT_DIVIDER;
	}

}
