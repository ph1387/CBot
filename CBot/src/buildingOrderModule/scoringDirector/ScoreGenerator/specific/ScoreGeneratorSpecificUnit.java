package buildingOrderModule.scoringDirector.ScoreGenerator.specific;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGenerator;
import buildingOrderModule.scoringDirector.gameState.GameState;
import buildingOrderModule.scoringDirector.gameState.GameStateSpecific_Unit;
import bwapi.UnitType;

/**
 * ScoreGeneratorSpecificUnit.java --- A {@link ScoreGenerator} applying a
 * target specific rate to the score. This rate is based on the {@link UnitType}
 * that is associated with the {@link GameState}.
 * 
 * @author P H - 03.10.2017
 *
 */
public abstract class ScoreGeneratorSpecificUnit extends ScoreGeneratorSpecific {

	public ScoreGeneratorSpecificUnit(BuildActionManager manager) {
		super(manager);
	}

	// -------------------- Functions

	protected UnitType extractUnitType(GameState gameState) throws Exception {
		return ((GameStateSpecific_Unit) gameState).getSpecificUnitType();
	}

	// ------------------------------ Getter / Setter

}
