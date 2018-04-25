package buildingOrderModule.scoringDirector.ScoreGenerator.specific;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGenerator;
import buildingOrderModule.scoringDirector.gameState.GameState;
import buildingOrderModule.scoringDirector.gameState.GameStateSpecific_Building;
import bwapi.UnitType;

/**
 * ScoreGeneratorSpecificBuilding.java --- A {@link ScoreGenerator} applying a
 * target specific rate to the score. This rate is based on the type of building
 * that is associated with the {@link GameState}.
 * 
 * @author P H - 12.03.2018
 *
 */
public abstract class ScoreGeneratorSpecificBuilding extends ScoreGeneratorSpecificUnit {

	public ScoreGeneratorSpecificBuilding(BuildActionManager manager) {
		super(manager);
	}

	// -------------------- Functions

	@Override
	protected UnitType extractUnitType(GameState gameState) throws Exception {
		return ((GameStateSpecific_Building) gameState).getBuilding();
	}

	// ------------------------------ Getter / Setter

}
