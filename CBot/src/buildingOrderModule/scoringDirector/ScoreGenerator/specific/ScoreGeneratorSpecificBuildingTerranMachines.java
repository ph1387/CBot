package buildingOrderModule.scoringDirector.ScoreGenerator.specific;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGenerator;
import buildingOrderModule.scoringDirector.ScoreGenerator.fixed.ScoreGeneratorFixed_Force;
import buildingOrderModule.scoringDirector.ScoreGenerator.fixed.ScoreGeneratorFixed_One;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget.ScoreGeneratorIncreaseNormal;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget.ScoreGeneratorIncreaseSlow;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget.ScoreGeneratorIncreaseVerySlow;
import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.UnitType;

/**
 * ScoreGeneratorSpecificBuildingTerranMachines.java --- A
 * {@link ScoreGenerator} applying a target specific rate to the score. This
 * class focuses on Terran machine-Unit buildings.
 * 
 * @author P H - 12.03.2018
 *
 */
public class ScoreGeneratorSpecificBuildingTerranMachines extends ScoreGeneratorSpecificBuilding {

	// TODO: UML ADD
	private ScoreGenerator scoreGeneratorFixedForce;
	private ScoreGenerator scoreGeneratorFixedOne;

	private ScoreGenerator scoreGeneratorIncreaseNormal;
	private ScoreGenerator scoreGeneratorIncreaseSlow;
	private ScoreGenerator scoreGeneratorIncreaseVerySlow;

	public ScoreGeneratorSpecificBuildingTerranMachines(BuildActionManager manager) {
		super(manager);

		this.scoreGeneratorFixedForce = new ScoreGeneratorFixed_Force(this.manager);
		this.scoreGeneratorFixedOne = new ScoreGeneratorFixed_One(this.manager);

		this.scoreGeneratorIncreaseNormal = new ScoreGeneratorIncreaseNormal(this.manager);
		this.scoreGeneratorIncreaseSlow = new ScoreGeneratorIncreaseSlow(this.manager);
		this.scoreGeneratorIncreaseVerySlow = new ScoreGeneratorIncreaseVerySlow(this.manager);
	}

	// -------------------- Functions

	@Override
	protected double generateScoreForGameState(GameState gameState, int framesPassed) throws Exception {
		UnitType unitType = this.extractUnitType(gameState);
		double score = 0.;

		// Supply_Depots not counted since they are not part of the simulation.
		switch (unitType.toString()) {
		// - Research / Upgrade:
		case "Terran_Academy":
			score = this.scoreGeneratorIncreaseVerySlow.generateScore(gameState, framesPassed);
			break;
		case "Terran_Engineering_Bay":
			score = this.scoreGeneratorIncreaseSlow.generateScore(gameState, framesPassed);
			break;
		case "Terran_Science_Facility":
			score = this.scoreGeneratorIncreaseNormal.generateScore(gameState, framesPassed);
			break;
		case "Terran_Armory":
			score = this.scoreGeneratorIncreaseSlow.generateScore(gameState, framesPassed);
			break;

		// - Addons:
		case "Terran_Control_Tower":
			score = this.scoreGeneratorFixedOne.generateScore(gameState, framesPassed);
			break;
		case "Terran_Machine_Shop":
			score = this.scoreGeneratorFixedOne.generateScore(gameState, framesPassed);
			break;

		// - Training:
		case "Terran_Command_Center":
			score = this.scoreGeneratorFixedOne.generateScore(gameState, framesPassed);
			break;
		case "Terran_Barracks":
			score = this.scoreGeneratorFixedOne.generateScore(gameState, framesPassed);
			break;
		case "Terran_Factory":
			score = this.scoreGeneratorFixedOne.generateScore(gameState, framesPassed);
			break;
		case "Terran_Starport":
			score = this.scoreGeneratorFixedOne.generateScore(gameState, framesPassed);
			break;

		// - Etc.:
		case "Terran_Refinery":
			score = this.scoreGeneratorFixedOne.generateScore(gameState, framesPassed);
			break;
		case "Terran_Missile_Turret":
			score = this.scoreGeneratorFixedForce.generateScore(gameState, framesPassed);
			break;
		case "Terran_Bunker":
			score = this.scoreGeneratorFixedForce.generateScore(gameState, framesPassed);
			break;

		default:
			throw new NullPointerException("Specific ScoreGeneration failed for UnitType: " + unitType);
		}

		return score;
	}

	@Override
	protected int generateDividerForGameState(GameState gameState, int framesPassed) throws Exception {
		UnitType unitType = this.extractUnitType(gameState);
		int divider = 1;
		
		if(unitType == UnitType.Terran_Missile_Turret || unitType == UnitType.Terran_Bunker) {
			divider = this.scoreGeneratorFixedForce.generateDivider(gameState, framesPassed);
		}
		
		return divider;
	}

}
