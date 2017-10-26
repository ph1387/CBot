package buildingOrderModule.scoringDirector.gameState;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoringDirector;
import bwapi.UnitType;

/**
 * GameStateSpecific_ImprovementFacility.java --- A GameState focused on
 * specific improvement facilities that are used for upgrading or researching.
 * 
 * @author P H - 03.10.2017
 *
 */
public class GameStateSpecific_ImprovementFacility extends GameState {

	private UnitType specificImprovementFacility;

	public GameStateSpecific_ImprovementFacility(UnitType unitType) {
		this.specificImprovementFacility = unitType;
	}

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateSpecificImprovementFacilityGenerator()
				.generateScore(this, this.updateFramesPassedScore);
	}

	@Override
	protected int generateDivider(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateSpecificImprovementFacilityGenerator()
				.generateDivider(this, this.updateFramesPassedDivider);
	}

	// ------------------------------ Getter / Setter

	public UnitType getSpecificImprovementFacility() {
		return specificImprovementFacility;
	}

}
