package buildingOrderModule.scoringDirector.ScoreGenerator.specific;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGenerator;
import buildingOrderModule.scoringDirector.gameState.GameState;
import buildingOrderModule.scoringDirector.gameState.GameStateSpecific_ImprovementFacility;
import bwapi.UnitType;

/**
 * ScoreGeneratorSpecificImprovementFacility.java --- A {@link ScoreGenerator}
 * applying a target specific rate to the score. This rate is based on the type
 * of improvement facility that is associated with the {@link GameState}.
 * 
 * @author P H - 03.10.2017
 *
 */
public abstract class ScoreGeneratorSpecificImprovementFacility extends ScoreGeneratorSpecificUnit {

	public ScoreGeneratorSpecificImprovementFacility(BuildActionManager manager) {
		super(manager);
	}

	// -------------------- Functions

	@Override
	protected UnitType extractUnitType(GameState gameState) throws Exception {
		return ((GameStateSpecific_ImprovementFacility) gameState).getSpecificImprovementFacility();
	}

	// ------------------------------ Getter / Setter

}
