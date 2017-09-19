package buildingOrderModule.scoringDirector;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGenerator;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGeneratorBuildingTerran;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGeneratorCheap;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGeneratorExpansionFocused;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGeneratorExpensive;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGeneratorFixed_One;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGeneratorFlying;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGeneratorGas;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGeneratorHealerTerran;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGeneratorMineral;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGeneratorRefinery;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGeneratorSupportTerran;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGeneratorTechnologyFocused;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGeneratorTrainingFacilitiesFree;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGeneratorTrainingFacilitiesIdle;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGeneratorUpgradeFocused;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGeneratorWorker;

// TODO: UML RENAME ScoreGeneratorFactoryTerran_Bio
/**
 * ScoreGeneratorFactoryTerranDefault.java --- a {@link ScoreGeneratorFactory} for
 * the Terran Race.
 * 
 * @author P H - 15.09.2017
 *
 */
public class ScoreGeneratorFactoryTerranDefault implements ScoreGeneratorFactory {

	private BuildActionManager manager;

	private ScoreGenerator scoreGeneratorExpansionFocused;
	private ScoreGenerator scoreGeneratorTechnologyFocused;
	private ScoreGenerator scoreGeneratorUpgradeFocused;
	private ScoreGenerator scoreGeneratorRefinery;
	// TODO: UML ADD
	private ScoreGenerator scoreGeneratorBuilding;
	private ScoreGenerator scoreGeneratorCheap;
	private ScoreGenerator scoreGeneratorExpensive;
	// TODO: UML ADD
	private ScoreGenerator scoreGeneratorFlying;

	public ScoreGeneratorFactoryTerranDefault(BuildActionManager manager) {
		this.manager = manager;

		this.scoreGeneratorExpansionFocused = new ScoreGeneratorExpansionFocused(this.manager);
		this.scoreGeneratorTechnologyFocused = new ScoreGeneratorTechnologyFocused(this.manager);
		this.scoreGeneratorUpgradeFocused = new ScoreGeneratorUpgradeFocused(this.manager);
		this.scoreGeneratorRefinery = new ScoreGeneratorRefinery(this.manager);
		this.scoreGeneratorBuilding = new ScoreGeneratorBuildingTerran(this.manager);
		this.scoreGeneratorCheap = new ScoreGeneratorCheap(this.manager);
		this.scoreGeneratorExpensive = new ScoreGeneratorExpensive(this.manager);
		this.scoreGeneratorFlying = new ScoreGeneratorFlying(this.manager);
	}

	// -------------------- Functions

	@Override
	public ScoreGenerator generateExpansionFocusedScoreGenerator() {
		return this.scoreGeneratorExpansionFocused;
	}

	@Override
	public ScoreGenerator generateTechnologyFocusedScoreGenerator() {
		return this.scoreGeneratorTechnologyFocused;
	}

	@Override
	public ScoreGenerator generateUpgradeFocusedScoreGenerator() {
		return this.scoreGeneratorUpgradeFocused;
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

	@Override
	public ScoreGenerator generateFlyingScoreGenerator() {
		return this.scoreGeneratorFlying;
	}

	@Override
	public ScoreGenerator generateBioScoreGenerator() {
		return new ScoreGeneratorFixed_One(this.manager);
	}

	@Override
	public ScoreGenerator generateMachineScoreGenerator() {
		return new ScoreGeneratorFixed_One(this.manager);
	}

	@Override
	public ScoreGenerator generateSupportScoreGenerator() {
		return new ScoreGeneratorSupportTerran(this.manager);
	}

	@Override
	public ScoreGenerator generateHealerScoreGenerator() {
		return new ScoreGeneratorHealerTerran(this.manager);
	}

	@Override
	public ScoreGenerator generateFreeTrainingFacilityScoreGenerator() {
		return new ScoreGeneratorTrainingFacilitiesFree(this.manager);
	}

	@Override
	public ScoreGenerator generateIdleTrainingFacilityScoreGenerator() {
		return new ScoreGeneratorTrainingFacilitiesIdle(this.manager);
	}

}