package buildingOrderModule.scoringDirector.gameState;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoringDirector;
import bwapi.UnitType;

/**
 * GameStateSpecific_Unit.java --- A GameState focused on training specific
 * {@link UnitType}s.
 * 
 * @author P H - 03.10.2017
 *
 */
public class GameStateSpecific_Unit extends GameState {

	private UnitType specificUnitType;

	public GameStateSpecific_Unit(UnitType unitType) {
		this.specificUnitType = unitType;
	}

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateSpecificUnitScoreGenerator().generateScore(this,
				this.updateFramesPassedScore);
	}

	@Override
	protected int generateDivider(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateSpecificUnitScoreGenerator().generateDivider(this,
				this.updateFramesPassedDivider);
	}

	// ------------------------------ Getter / Setter

	public UnitType getSpecificUnitType() {
		return specificUnitType;
	}

}
