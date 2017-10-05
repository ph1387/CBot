package buildingOrderModule.scoringDirector.ScoreGenerator;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.ScoreGeneratorGradualChangeMaxReset;
import buildingOrderModule.scoringDirector.gameState.GameState;

/**
 * ScoreGeneratorTechnologyFocused.java --- A {@link ScoreGenerator} focused on
 * technologies. Increases it's score until the number of technologies changes
 * or no more technologies can be researched.
 * 
 * @author P H - 16.09.2017
 *
 */
public class ScoreGeneratorTechnologyFocused extends ScoreGeneratorGradualChangeMaxReset {

	private static double DefaultRate = 0.1;
	// TODO: UML CHANGE 200
	private static double DefaultFrameDiff = 400;
	private static double DefaultResetValue = 0.;

	// The number of technologies researched by the Bot.
	private int techCountPrev = 0;
	// Flag signaling if all technologies are researched (No more technologies
	// can be researched and therefore the score does not need to be changed).
	private boolean techsFinished = false;

	public ScoreGeneratorTechnologyFocused(BuildActionManager manager) {
		super(manager, DefaultRate, DefaultFrameDiff, DefaultResetValue);
	}

	// -------------------- Functions

	@Override
	protected boolean shouldReset(GameState gameState) {
		int techCountCurrent = this.manager.getCurrentGameInformation().getCurrentTechs().size();
		int techCountMax = this.manager.getDesiredTechs().size();
		boolean reset = false;

		// Reset the score after the previously stored number of technologies
		// does not match the current number of technologies. This means that a
		// new technology has been researched and therefore the Bot does not
		// immediately need to research another one.
		if (techCountCurrent != this.techCountPrev) {
			this.techCountPrev = techCountCurrent;
			reset = true;
		}

		// Check if all possible technologies have been researched.
		if (techCountCurrent == techCountMax && !this.techsFinished) {
			this.techsFinished = true;
		}

		return reset;
	}

	@Override
	protected boolean isThresholdReached(double score) {
		// The threshold is never reached. The rate is constantly applied to the
		// score and therefore the Bot is bound to research technologies
		// eventually until all required ones have been researched by the Bot.
		return this.techsFinished;
	}

}
