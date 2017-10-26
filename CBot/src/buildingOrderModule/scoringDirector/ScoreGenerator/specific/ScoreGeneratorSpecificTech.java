package buildingOrderModule.scoringDirector.ScoreGenerator.specific;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGenerator;
import buildingOrderModule.scoringDirector.gameState.GameState;
import buildingOrderModule.scoringDirector.gameState.GameStateSpecific_Tech;
import bwapi.TechType;

/**
 * ScoreGeneratorSpecificTech.java --- A {@link ScoreGenerator} applying a
 * target specific rate to the score. This rate is based on the {@link TechType}
 * that is associated with the {@link GameState}.
 * 
 * @author P H - 03.10.2017
 *
 */
public abstract class ScoreGeneratorSpecificTech extends ScoreGeneratorSpecific {

	public ScoreGeneratorSpecificTech(BuildActionManager manager) {
		super(manager);
	}

	// -------------------- Functions

	protected TechType extractTechType(GameState gameState) throws Exception {
		return ((GameStateSpecific_Tech) gameState).getSpecificTechType();
	}

	// ------------------------------ Getter / Setter

}
