package buildingOrderModule.scoringDirector;

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

	public GameStateFocused_Technology() {
		super(ScoreStart, Rate, FrameDiff);
	}

	// -------------------- Functions

	@Override
	protected boolean shouldReset(ScoringDirector scoringDirector, GameStateCurrentInformation currenInformation) {
		int techCountCurrent = currenInformation.getCurrentTechs().size();
		boolean reset = false;

		// Reset the score after the previously stored number of technologies
		// does not match the current number of technologies. This means that a
		// new technology has been researched and therefore the Bot does not
		// immediately need to research another one.
		if (techCountCurrent != this.techCountPrev) {
			this.techCountPrev = techCountCurrent;
			reset = true;
		}

		return reset;
	}

	@Override
	protected boolean isTresholdReached(double score) {
		// The threshold is never reached. The rate is constantly applied to the
		// score and therefore the Bot is bound to research technologies
		// eventually.
		return false;
	}

	// TODO: WIP REMOVE
	@Override
	protected double generateScore(ScoringDirector scoringDirector, GameStateCurrentInformation currenInformation) {
		double value = super.generateScore(scoringDirector, currenInformation);

		// TODO: WIP REMOVE
		System.out.println("GameState TechnologyFocused: " + value);

		return value;
	}

}
