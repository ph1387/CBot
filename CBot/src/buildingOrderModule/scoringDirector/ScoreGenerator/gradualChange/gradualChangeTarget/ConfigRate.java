package buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget;

// TODO: UML ADD
// TODO: UML VISIBILITY
/**
 * ConfigRate.java --- Configuration for the increase / decrease
 * {@link ScoreGeneratorGradualChangeTarget}s.
 * 
 * @author P H - 01.10.2017
 *
 */
class ConfigRate {

	public static final double RATE = 0.1;

	public static final double FRAME_DIFF_VERY_FAST = 200;
	public static final double FRAME_DIFF_FAST = 2 * FRAME_DIFF_VERY_FAST;
	public static final double FRAME_DIFF_NORMAL = 2 * FRAME_DIFF_FAST;
	public static final double FRAME_DIFF_SLOW = 2 * FRAME_DIFF_NORMAL;
	public static final double FRAME_DIFF_VERY_SLOW = 2 * FRAME_DIFF_SLOW;

	public static final double MAX_INCREASE = 1.0;
	public static final double MAX_DECREASE = 0.0;
}
