package buildingOrderModule.scoringDirector.gameState;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoringDirector;
import bwapi.UnitType;

// TODO: UML ADD
// TODO: UML REMOVE GameStateSpecific_ImprovementFacility
/**
 * GameStateSpecific_Building.java --- A GameState focused on specific
 * buildings.
 * 
 * @author P H - 12.03.2018
 *
 */
public class GameStateSpecific_Building extends GameState {

	private UnitType building;

	public GameStateSpecific_Building(UnitType unitType) {
		this.building = unitType;
	}

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateSpecificBuildingGenerator().generateScore(this,
				this.updateFramesPassedScore);
	}

	@Override
	protected int generateDivider(ScoringDirector scoringDirector, BuildActionManager manager) {
		return scoringDirector.getScoreGeneratorFactory().generateSpecificBuildingGenerator().generateDivider(this,
				this.updateFramesPassedDivider);
	}

	// ------------------------------ Getter / Setter

	public UnitType getBuilding() {
		return building;
	}

}
