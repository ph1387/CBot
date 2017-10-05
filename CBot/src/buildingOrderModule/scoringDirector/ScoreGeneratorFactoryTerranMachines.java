package buildingOrderModule.scoringDirector;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGenerator;
import buildingOrderModule.scoringDirector.ScoreGenerator.fixed.ScoreGeneratorFixed_Null;
import buildingOrderModule.scoringDirector.ScoreGenerator.fixed.ScoreGeneratorFixed_One;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget.ScoreGeneratorFlying;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget.ScoreGeneratorIncreaseSlow;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget.ScoreGeneratorIncreaseVerySlow;
import buildingOrderModule.scoringDirector.ScoreGenerator.proportion.ScoreGeneratorHealerTerran;
import buildingOrderModule.scoringDirector.ScoreGenerator.proportion.ScoreGeneratorSupportTerran;
import buildingOrderModule.scoringDirector.ScoreGenerator.specific.ScoreGeneratorSpecificImprovementFacilityTerranMachines;
import buildingOrderModule.scoringDirector.ScoreGenerator.specific.ScoreGeneratorSpecificTechTerranMachines;
import buildingOrderModule.scoringDirector.ScoreGenerator.specific.ScoreGeneratorSpecificUnitTerranMachines;
import buildingOrderModule.scoringDirector.ScoreGenerator.specific.ScoreGeneratorSpecificUpgradeTerranMachines;

// TODO: UML ADD
/**
 * ScoreGeneratorFactoryTerranMachines.java --- a {@link ScoreGeneratorFactory}
 * for the Terran Race with the focus being on machine Units.
 * 
 * @author P H - 01.10.2017
 *
 */
public class ScoreGeneratorFactoryTerranMachines extends ScoreGeneratorFactoryTerranDefault {

	private ScoreGenerator scoreGeneratorFlying;
	private ScoreGenerator scoreGeneratorResearchMachines;
	private ScoreGenerator scoreGeneratorResearchFlying;
	private ScoreGenerator scoreGeneratorUpgradeMachines;
	private ScoreGenerator scoreGeneratorUpgradeFlying;

	private ScoreGenerator scoreGeneratorSpecificTech;
	private ScoreGenerator scoreGeneratorSpecificUpgrade;
	private ScoreGenerator scoreGeneratorSpecificUnit;
	private ScoreGenerator scoreGeneratorSpecificImprovementFacility;

	public ScoreGeneratorFactoryTerranMachines(BuildActionManager manager) {
		super(manager);

		this.scoreGeneratorFlying = new ScoreGeneratorFlying(this.manager);
		this.scoreGeneratorResearchMachines = new ScoreGeneratorIncreaseSlow(this.manager);
		this.scoreGeneratorResearchFlying = new ScoreGeneratorIncreaseVerySlow(this.manager);
		this.scoreGeneratorUpgradeMachines = new ScoreGeneratorIncreaseSlow(this.manager);
		this.scoreGeneratorUpgradeFlying = new ScoreGeneratorIncreaseVerySlow(this.manager);

		this.scoreGeneratorSpecificTech = new ScoreGeneratorSpecificTechTerranMachines(this.manager);
		this.scoreGeneratorSpecificUpgrade = new ScoreGeneratorSpecificUpgradeTerranMachines(this.manager);
		this.scoreGeneratorSpecificUnit = new ScoreGeneratorSpecificUnitTerranMachines(this.manager);
		this.scoreGeneratorSpecificImprovementFacility = new ScoreGeneratorSpecificImprovementFacilityTerranMachines(
				this.manager);
	}

	// -------------------- Functions

	// -------------------- Macro: General behavior of the Bot.

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

	// ------------------------------ Technologies
	@Override
	public ScoreGenerator generateResearchMachinesScoreGenerator() {
		return this.scoreGeneratorResearchMachines;
	}

	@Override
	public ScoreGenerator generateResearchBioScoreGenerator() {
		return new ScoreGeneratorFixed_Null(this.manager);
	}

	@Override
	public ScoreGenerator generateResearchFlyingScoreGenerator() {
		return this.scoreGeneratorResearchFlying;
	}

	// ------------------------------ Upgrades
	@Override
	public ScoreGenerator generateUpgradesMachinesScoreGenerator() {
		return this.scoreGeneratorUpgradeMachines;
	}

	@Override
	public ScoreGenerator generateUpgradesBioScoreGenerator() {
		return new ScoreGeneratorFixed_Null(this.manager);
	}

	@Override
	public ScoreGenerator generateUpgradesFlyingScoreGenerator() {
		return this.scoreGeneratorUpgradeFlying;
	}

	// -------------------- Micro: Targeted at specific features.

	@Override
	public ScoreGenerator generateSpecificTechScoreGenerator() {
		return this.scoreGeneratorSpecificTech;
	}

	@Override
	public ScoreGenerator generateSpecificUpgradeScoreGenerator() {
		return this.scoreGeneratorSpecificUpgrade;
	}

	@Override
	public ScoreGenerator generateSpecificUnitScoreGenerator() {
		return this.scoreGeneratorSpecificUnit;
	}

	@Override
	public ScoreGenerator generateSpecificImprovementFacilityGenerator() {
		return this.scoreGeneratorSpecificImprovementFacility;
	}

}
