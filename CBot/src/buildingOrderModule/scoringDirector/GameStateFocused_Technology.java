package buildingOrderModule.scoringDirector;

import buildingOrderModule.buildActionManagers.BuildActionManager;

// TODO: UML ADD NOT PUBLIC
/**
 * GameStateFocused_Technology.java --- A GameState focused on researching
 * technologies.
 * 
 * @author P H - 16.07.2017
 *
 */
class GameStateFocused_Technology extends GameStateGradualChangeWithReset {

	// The starting score of this GameState.
	private static double ScoreStart = 0.;
	// The rate that is applied to the score in each iteration.
	private static double Rate = 0.1;
	// The frames after the rate is applied.
	private static double FrameDiff = 200;

	// The number of technologies researched by the Bot.
	private int techCountPrev = 0;
	// Flag signaling if all technologies are researched (No more technologies
	// can be researched and therefore the score does not need to be changed).
	private boolean techsFinished = false;

	public GameStateFocused_Technology() {
		super(ScoreStart, Rate, FrameDiff);
	}

	// -------------------- Functions

	@Override
	protected boolean shouldReset(ScoringDirector scoringDirector, BuildActionManager manager) {
		int techCountCurrent = manager.getCurrentGameInformation().getCurrentTechs().size();
		int techCountMax = manager.getDesiredTechs().size();
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
	protected boolean isTresholdReached(double score) {
		// The threshold is never reached. The rate is constantly applied to the
		// score and therefore the Bot is bound to research technologies
		// eventually until all required ones have been researched by the Bot.
		return this.techsFinished;
	}

}
