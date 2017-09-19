package buildingOrderModule.scoringDirector.ScoreGenerator;

import java.util.List;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.UnitType;

// TODO: UML ADD
/**
 * ScoreGeneratorBuilding.java --- A {@link ScoreGenerator} focused on
 * buildings. Increases it's score until the number of buildings changes.
 * 
 * @author P H - 19.09.2017
 *
 */
public class ScoreGeneratorBuilding extends ScoreGeneratorGradualChangeMaxReset {

	private List<UnitType> buildingTypes;
	private int buildingCountPrev = 0;

	public ScoreGeneratorBuilding(BuildActionManager manager, double rate, double frameDiff, double resetValue,
			List<UnitType> buildingTypes) {
		super(manager, rate, frameDiff, resetValue);

		this.buildingTypes = buildingTypes;
	}

	// -------------------- Functions

	@Override
	protected boolean shouldReset(GameState gameState) {
		int currentBuildingCount = 0;
		boolean reset = false;

		for (UnitType unitType : this.buildingTypes) {
			currentBuildingCount += this.manager.getInformationStorage().getCurrentGameInformation()
					.getCurrentUnitCounts().getOrDefault(unitType, 0);
		}

		// Act based on the current count of buildings compared to the stored,
		// previous one.
		if (currentBuildingCount != this.buildingCountPrev) {
			reset = true;
		}
		this.buildingCountPrev = currentBuildingCount;

		return reset;
	}

	@Override
	protected boolean isThresholdReached(double score) {
		return false;
	}

}
