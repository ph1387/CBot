package buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGenerator;

//TODO: UML MARK
@Deprecated
/**
 * ScoreGeneratorExpensive.java --- A {@link ScoreGenerator} regarding expensive
 * Units.
 * 
 * @author P H - 16.09.2017
 *
 */
public class ScoreGeneratorExpensive extends ScoreGeneratorGradualChangeTarget {

	private static double DefaultRate = 0.1;
	private static double DefaultFrameDiff = 2000;
	private static double DefaultTargetValue = 1.;

	public ScoreGeneratorExpensive(BuildActionManager manager) {
		super(manager, DefaultRate, DefaultFrameDiff, DefaultTargetValue);
	}

	// -------------------- Functions

}
