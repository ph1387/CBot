package buildingOrderModule.scoringDirector;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGenerator;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGeneratorGas;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGeneratorMineral;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGeneratorTrainingFacilitiesFree;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGeneratorTrainingFacilitiesIdle;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGeneratorWorker;
import buildingOrderModule.scoringDirector.ScoreGenerator.fixed.ScoreGeneratorFixed_One;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeMaxReset.ScoreGeneratorBuildingTerran;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeMaxReset.ScoreGeneratorExpansionFocused;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeMaxReset.ScoreGeneratorRefinery;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeMaxReset.ScoreGeneratorTechnologyFocused;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeMaxReset.ScoreGeneratorUpgradeFocused;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget.ScoreGeneratorIncreaseNormal;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget.ScoreGeneratorIncreaseSlow;

/**
 * ScoreGeneratorFactoryTerranDefault.java --- a {@link ScoreGeneratorFactory}
 * for the Terran Race.
 * 
 * @author P H - 15.09.2017
 *
 */
public abstract class ScoreGeneratorFactoryTerranDefault implements ScoreGeneratorFactory {

	protected BuildActionManager manager;

	private ScoreGenerator scoreGeneratorExpansionFocused;
	private ScoreGenerator scoreGeneratorTechnologyFocused;
	private ScoreGenerator scoreGeneratorUpgradeFocused;
	private ScoreGenerator scoreGeneratorRefinery;
	private ScoreGenerator scoreGeneratorBuilding;
	private ScoreGenerator scoreGeneratorCheap;
	private ScoreGenerator scoreGeneratorExpensive;

	public ScoreGeneratorFactoryTerranDefault(BuildActionManager manager) {
		this.manager = manager;

		this.scoreGeneratorExpansionFocused = new ScoreGeneratorExpansionFocused(this.manager);
		this.scoreGeneratorTechnologyFocused = new ScoreGeneratorTechnologyFocused(this.manager);
		this.scoreGeneratorUpgradeFocused = new ScoreGeneratorUpgradeFocused(this.manager);
		this.scoreGeneratorRefinery = new ScoreGeneratorRefinery(this.manager);
		this.scoreGeneratorBuilding = new ScoreGeneratorBuildingTerran(this.manager);
		this.scoreGeneratorCheap = new ScoreGeneratorIncreaseNormal(this.manager);
		this.scoreGeneratorExpensive = new ScoreGeneratorIncreaseSlow(this.manager);
	}

	// -------------------- Functions

	// -------------------- Macro: General behavior of the Bot.

	@Override
	public ScoreGenerator generateExpansionFocusedScoreGenerator() {
		return this.scoreGeneratorExpansionFocused;
	}

	@Override
	public ScoreGenerator generateRefineryScoreGenerator() {
		return this.scoreGeneratorRefinery;
	}

	@Override
	public ScoreGenerator generateBuildingScoreGenerator() {
		return this.scoreGeneratorBuilding;
	}

	@Override
	public ScoreGenerator generateWorkerScoreGenerator() {
		return new ScoreGeneratorWorker(this.manager);
	}

	@Override
	public ScoreGenerator generateCombatScoreGenerator() {
		return new ScoreGeneratorFixed_One(this.manager);
	}

	@Override
	public ScoreGenerator generateCheapScoreGenerator() {
		return this.scoreGeneratorCheap;
	}

	@Override
	public ScoreGenerator generateExpensiveScoreGenerator() {
		return this.scoreGeneratorExpensive;
	}

	@Override
	public ScoreGenerator generateMineralScoreGenerator() {
		return new ScoreGeneratorMineral(this.manager);
	}

	@Override
	public ScoreGenerator generateGasScoreGenerator() {
		return new ScoreGeneratorGas(this.manager);
	}

	// ------------------------------ Technologies
	@Override
	public ScoreGenerator generateTechnologyFocusedScoreGenerator() {
		return this.scoreGeneratorTechnologyFocused;
	}

	// ------------------------------ Upgrades
	@Override
	public ScoreGenerator generateUpgradeFocusedScoreGenerator() {
		return this.scoreGeneratorUpgradeFocused;
	}

	// -------------------- Micro: Targeted at specific features.

	@Override
	public ScoreGenerator generateFreeTrainingFacilityScoreGenerator() {
		return new ScoreGeneratorTrainingFacilitiesFree(this.manager);
	}

	@Override
	public ScoreGenerator generateIdleTrainingFacilityScoreGenerator() {
		return new ScoreGeneratorTrainingFacilitiesIdle(this.manager);
	}

}
