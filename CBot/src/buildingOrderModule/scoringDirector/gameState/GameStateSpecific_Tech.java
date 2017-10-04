package buildingOrderModule.scoringDirector.gameState;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoringDirector;
import bwapi.TechType;

//TODO: UML ADD
/**
 * GameStateSpecific_Tech.java --- A GameState focused on researching specific
 * {@link TechType}s.
 * 
 * @author P H - 03.10.2017
 *
 */
public class GameStateSpecific_Tech extends GameState {

	private TechType specificTechType;

	public GameStateSpecific_Tech(TechType techType) {
		this.specificTechType = techType;
	}

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateSpecificTechScoreGenerator().generateScore(this,
				this.updateFramesPassedScore);
	}

	@Override
	protected int generateDivider(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateSpecificTechScoreGenerator().generateDivider(this,
				this.updateFramesPassedDivider);
	}

	// ------------------------------ Getter / Setter

	public TechType getSpecificTechType() {
		return specificTechType;
	}

}
