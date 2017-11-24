package buildingOrderModule.scoringDirector;

import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGenerator;

/**
 * ScoreGeneratorFactory.java --- A Interface defining a factory for
 * {@link ScoreGenerator}s (Abstract Factory Pattern). </br>
 * <b>Note:</b></br> The objects returned by this factory must only be generated
 * <b>once</b> for any factory that relies on storing information!
 * 
 * @author P H - 16.07.2017
 *
 */
public interface ScoreGeneratorFactory {

	// -------------------- Macro: General behavior of the Bot.

	/**
	 *
	 * @return a {@link ScoreGenerator} for expansion.
	 */
	public ScoreGenerator generateExpansionFocusedScoreGenerator();

	/**
	 *
	 * @return a {@link ScoreGenerator} for refineries.
	 */
	public ScoreGenerator generateRefineryScoreGenerator();

	/**
	 *
	 * @return a {@link ScoreGenerator} for buildings.
	 */
	public ScoreGenerator generateBuildingScoreGenerator();

	/**
	 *
	 * @return a {@link ScoreGenerator} for workers.
	 */
	public ScoreGenerator generateWorkerScoreGenerator();

	/**
	 *
	 * @return a {@link ScoreGenerator} for combat units.
	 */
	public ScoreGenerator generateCombatScoreGenerator();

	/**
	 *
	 * @return a {@link ScoreGenerator} for cheap units.
	 */
	public ScoreGenerator generateCheapScoreGenerator();

	/**
	 *
	 * @return a {@link ScoreGenerator} for expensive units.
	 */
	public ScoreGenerator generateExpensiveScoreGenerator();

	/**
	 *
	 * @return a {@link ScoreGenerator} for units requiring minerals.
	 */
	public ScoreGenerator generateMineralScoreGenerator();

	/**
	 *
	 * @return a {@link ScoreGenerator} for units requiring gas.
	 */
	public ScoreGenerator generateGasScoreGenerator();

	/**
	 *
	 * @return a {@link ScoreGenerator} for flying units.
	 */
	public ScoreGenerator generateFlyingScoreGenerator();

	/**
	 *
	 * @return a {@link ScoreGenerator} for bio units.
	 */
	public ScoreGenerator generateBioScoreGenerator();

	/**
	 *
	 * @return a {@link ScoreGenerator} for mechanical units.
	 */
	public ScoreGenerator generateMachineScoreGenerator();

	/**
	 *
	 * @return a {@link ScoreGenerator} for support units.
	 */
	public ScoreGenerator generateSupportScoreGenerator();

	/**
	 *
	 * @return a {@link ScoreGenerator} for healer units.
	 */
	public ScoreGenerator generateHealerScoreGenerator();

	// ------------------------------ Technologies
	/**
	 *
	 * @return a {@link ScoreGenerator} for technologies.
	 */
	public ScoreGenerator generateTechnologyFocusedScoreGenerator();

	/**
	 *
	 * @return a {@link ScoreGenerator} for researching machine Unit
	 *         technologies.
	 */
	public ScoreGenerator generateResearchMachinesScoreGenerator();

	/**
	 *
	 * @return a {@link ScoreGenerator} for researching bio Unit technologies.
	 */
	public ScoreGenerator generateResearchBioScoreGenerator();

	/**
	 *
	 * @return a {@link ScoreGenerator} for researching flying Unit
	 *         technologies.
	 */
	public ScoreGenerator generateResearchFlyingScoreGenerator();

	// ------------------------------ Upgrades
	/**
	 *
	 * @return a {@link ScoreGenerator} for upgrades.
	 */
	public ScoreGenerator generateUpgradeFocusedScoreGenerator();

	/**
	 *
	 * @return a {@link ScoreGenerator} for upgrading machine Units.
	 */
	public ScoreGenerator generateUpgradesMachinesScoreGenerator();

	/**
	 *
	 * @return a {@link ScoreGenerator} for upgrading bio Units.
	 */
	public ScoreGenerator generateUpgradesBioScoreGenerator();

	/**
	 *
	 * @return a {@link ScoreGenerator} for upgrading flying Units.
	 */
	public ScoreGenerator generateUpgradesFlyingScoreGenerator();

	// -------------------- Micro: Targeted at specific features.

	/**
	 *
	 * @return a {@link ScoreGenerator} for free training facilities (In a
	 *         <b>positive</b> way!). The more facilities available the higher
	 *         the score.
	 */
	public ScoreGenerator generateFreeTrainingFacilityScoreGenerator();

	/**
	 *
	 * @return a {@link ScoreGenerator} for idling training facilities (In a
	 *         <b>negative</b> way!). The more facilities available the lower
	 *         the score.
	 */
	public ScoreGenerator generateIdleTrainingFacilityScoreGenerator();

	/**
	 *
	 * @return a {@link ScoreGenerator} for specific TechTypes.
	 */
	public ScoreGenerator generateSpecificTechScoreGenerator();

	/**
	 *
	 * @return a {@link ScoreGenerator} for specific UpgradeTypes.
	 */
	public ScoreGenerator generateSpecificUpgradeScoreGenerator();

	/**
	 *
	 * @return a {@link ScoreGenerator} for specific UnitTypes.
	 */
	public ScoreGenerator generateSpecificUnitScoreGenerator();

	/**
	 *
	 * @return a {@link ScoreGenerator} for specific improvement facilities
	 *         which are used to either research new technologies or upgrade
	 *         existing ones.
	 */
	public ScoreGenerator generateSpecificImprovementFacilityGenerator();

}
