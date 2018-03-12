package buildingOrderModule.scoringDirector;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGenerator;
import buildingOrderModule.scoringDirector.ScoreGenerator.fixed.ScoreGeneratorFixed_Null;
import buildingOrderModule.scoringDirector.ScoreGenerator.fixed.ScoreGeneratorFixed_One;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget.ScoreGeneratorFlying;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget.ScoreGeneratorIncreaseNormal;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget.ScoreGeneratorIncreaseSlow;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget.ScoreGeneratorIncreaseVerySlow;
import buildingOrderModule.scoringDirector.ScoreGenerator.proportion.ScoreGeneratorHealerTerran;
import buildingOrderModule.scoringDirector.ScoreGenerator.proportion.ScoreGeneratorSupportTerran;
import buildingOrderModule.scoringDirector.ScoreGenerator.specific.ScoreGeneratorSpecificBuildingTerranBio;
import buildingOrderModule.scoringDirector.ScoreGenerator.specific.ScoreGeneratorSpecificTechTerranBio;
import buildingOrderModule.scoringDirector.ScoreGenerator.specific.ScoreGeneratorSpecificUnitTerranBio;
import buildingOrderModule.scoringDirector.ScoreGenerator.specific.ScoreGeneratorSpecificUpgradeTerranBio;

/**
 * ScoreGeneratorFactoryTerranBio.java --- a {@link ScoreGeneratorFactory} for
 * the Terran Race with the focus being on bio Units.
 * 
 * @author P H - 18.11.2017
 *
 */
public class ScoreGeneratorFactoryTerranBio extends ScoreGeneratorFactoryTerranDefault {

	private ScoreGenerator scoreGeneratorFlying;
	private ScoreGenerator scoreGeneratorResearchMachines;
	private ScoreGenerator scoreGeneratorResearchBio;
	private ScoreGenerator scoreGeneratorResearchFlying;
	private ScoreGenerator scoreGeneratorUpgradeBio;
	private ScoreGenerator scoreGeneratorUpgradeFlying;

	private ScoreGenerator scoreGeneratorSpecificTech;
	private ScoreGenerator scoreGeneratorSpecificUpgrade;
	private ScoreGenerator scoreGeneratorSpecificUnit;
	// TODO: UML RENAME scoreGeneratorSpecificImprovementFacility
	private ScoreGenerator scoreGeneratorSpecificBuilding;

	public ScoreGeneratorFactoryTerranBio(BuildActionManager manager) {
		super(manager);

		this.scoreGeneratorFlying = new ScoreGeneratorFlying(this.manager);
		this.scoreGeneratorResearchMachines = new ScoreGeneratorIncreaseSlow(this.manager);
		this.scoreGeneratorResearchBio = new ScoreGeneratorIncreaseNormal(this.manager);
		this.scoreGeneratorResearchFlying = new ScoreGeneratorIncreaseNormal(this.manager);
		this.scoreGeneratorUpgradeBio = new ScoreGeneratorIncreaseVerySlow(this.manager);
		this.scoreGeneratorUpgradeFlying = new ScoreGeneratorIncreaseVerySlow(this.manager);

		this.scoreGeneratorSpecificTech = new ScoreGeneratorSpecificTechTerranBio(this.manager);
		this.scoreGeneratorSpecificUpgrade = new ScoreGeneratorSpecificUpgradeTerranBio(this.manager);
		this.scoreGeneratorSpecificUnit = new ScoreGeneratorSpecificUnitTerranBio(this.manager);
		this.scoreGeneratorSpecificBuilding = new ScoreGeneratorSpecificBuildingTerranBio(this.manager);
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
		return new ScoreGeneratorIncreaseVerySlow(this.manager);
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
		return this.scoreGeneratorResearchBio;
	}

	@Override
	public ScoreGenerator generateResearchFlyingScoreGenerator() {
		return this.scoreGeneratorResearchFlying;
	}

	// ------------------------------ Upgrades
	@Override
	public ScoreGenerator generateUpgradesMachinesScoreGenerator() {
		return new ScoreGeneratorFixed_Null(this.manager);
	}

	@Override
	public ScoreGenerator generateUpgradesBioScoreGenerator() {
		return this.scoreGeneratorUpgradeBio;
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
	public ScoreGenerator generateSpecificBuildingGenerator() {
		return this.scoreGeneratorSpecificBuilding;
	}
}
