package buildingOrderModule.scoringDirector;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGenerator;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGeneratorCheap;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGeneratorExpansionFocused;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGeneratorExpensive;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGeneratorFixed_One;
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

// TODO: WIP FOCUS ON BIO UNITS
/**
 * ScoreGeneratorFactoryTerran_Bio.java --- a {@link ScoreGeneratorFactory} for
 * the Terran Race utilizing /focusing on mainly Bio-Units.
 * 
 * @author P H - 15.09.2017
 *
 */
public class ScoreGeneratorFactoryTerran_Bio implements ScoreGeneratorFactory {

	private BuildActionManager manager;

	private ScoreGenerator scoreGeneratorExpansionFocused;
	private ScoreGenerator scoreGeneratorTechnologyFocused;
	private ScoreGenerator scoreGeneratorUpgradeFocused;
	private ScoreGenerator scoreGeneratorRefinery;
	private ScoreGenerator scoreGeneratorCheap;
	private ScoreGenerator scoreGeneratorExpensive;

	public ScoreGeneratorFactoryTerran_Bio(BuildActionManager manager) {
		this.manager = manager;

		this.scoreGeneratorExpansionFocused = new ScoreGeneratorExpansionFocused(this.manager);
		this.scoreGeneratorTechnologyFocused = new ScoreGeneratorTechnologyFocused(this.manager);
		this.scoreGeneratorUpgradeFocused = new ScoreGeneratorUpgradeFocused(this.manager);
		this.scoreGeneratorRefinery = new ScoreGeneratorRefinery(this.manager);
		this.scoreGeneratorCheap = new ScoreGeneratorCheap(this.manager);
		this.scoreGeneratorExpensive = new ScoreGeneratorExpensive(this.manager);
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
		// TODO: WIP ADD OTHER CLASS
		return new ScoreGeneratorFixed_One(this.manager);
	}

	@Override
	public ScoreGenerator generateWorkerScoreGenerator() {
		return new ScoreGeneratorWorker(this.manager);
	}

	@Override
	public ScoreGenerator generateCombatScoreGenerator() {
		// TODO: WIP ADD OTHER CLASS
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
		// TODO: WIP ADD OTHER CLASS
		return new ScoreGeneratorFixed_One(this.manager);
	}

	@Override
	public ScoreGenerator generateBioScoreGenerator() {
		// TODO: WIP ADD OTHER CLASS
		return new ScoreGeneratorFixed_One(this.manager);
	}

	@Override
	public ScoreGenerator generateMachineScoreGenerator() {
		// TODO: WIP ADD OTHER CLASS
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
