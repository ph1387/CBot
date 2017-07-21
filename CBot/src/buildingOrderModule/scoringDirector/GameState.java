package buildingOrderModule.scoringDirector;

import java.util.HashMap;
import java.util.HashSet;

import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;

// TODO: UML ADD
/**
 * GameState.java --- Class for representing a state in the game with an
 * associated score attached to it. Each state has a score attached to it that
 * resembles the importance of it. The higher the score the better.
 * 
 * @author P H - 16.07.2017
 *
 */
public abstract class GameState {

	// States that can be used by other actions for generating their individual
	// score:
	public static final GameState Resource_Focused = new GameStateFocused_Resources();
	public static final GameState Military_Focused = new GameStateFocused_Military();
	public static final GameState Expansion_Focused = new GameStateFocused_Expansion();

	public static final GameState Technology_Focused = new GameStateFocused_Technology();
	public static final GameState Upgrade_Focused = new GameStateFocused_Upgrade();

	public static final GameState Building_Units = new GameStateUnits_Building();
	public static final GameState Worker_Units = new GameStateUnits_Worker();
	public static final GameState Combat_Units = new GameStateUnits_Combat();

	public static final GameState Cheap_Units = new GameStateUnits_Cheap();
	public static final GameState Expensive_Units = new GameStateUnits_Expensive();
	public static final GameState Mineral_Units = new GameStateUnits_Mineral();
	public static final GameState Gas_Units = new GameStateUnits_Gas();

	public static final GameState Flying_Units = new GameStateUnits_Flying();
	public static final GameState Bio_Units = new GameStateUnits_Bio();
	public static final GameState Support_Units = new GameStateUnits_Support();
	public static final GameState Healer_Units = new GameStateUnits_Healer();

	// The current score this state holds.
	private double currentScore = 0.;

	// TODO: UML ADD ~
	GameState() {

	}

	// -------------------- Functions

	/**
	 * Function for updating the current multiplier of the GameState.
	 *
	 * @param desiredBuildingsPercent
	 *            the desired percentage of buildings.
	 * @param desiredCombatUnitsPercent
	 *            the desired percentage of combat Units.
	 * @param desiredTechs
	 *            the TechTypes that the manager should research.
	 * @param desiredUpgrades
	 *            the UpgradeTypes the manager should pursue.
	 * @param currentWorkerPercent
	 *            the actual current percentage of worker Units.
	 * @param currentBuildingsPercent
	 *            the actual current percentage of buildings.
	 * @param currentCombatUnitsPercent
	 *            the actual current percentage of combat Units.
	 * @param currentUnits
	 *            all current Units.
	 * @param currentTechs
	 *            all currently researched TechTypes of the desired ones.
	 * @param currentUpgrades
	 *            all currently performed UpgradeTypes of the desired ones.
	 */
	public void updateMultiplier(double desiredBuildingsPercent,
			double desiredCombatUnitsPercent, HashSet<TechType> desiredTechs,
			HashMap<UpgradeType, Integer> desiredUpgrades, double currentWorkerPercent, double currentBuildingsPercent,
			double currentCombatUnitsPercent, HashMap<UnitType, Integer> currentUnits, HashSet<TechType> currentTechs,
			HashMap<UpgradeType, Integer> currentUpgrades) {
		this.currentScore = this.generateScore(desiredBuildingsPercent, desiredCombatUnitsPercent,
				desiredTechs, desiredUpgrades, currentWorkerPercent, currentBuildingsPercent, currentCombatUnitsPercent,
				currentUnits, currentTechs, currentUpgrades);
	}

	/**
	 * Function for generating a new score for the GameState that represents the
	 * state of the game in the are that the GameState is responsible for. This
	 * score (For simplicity) should be between 0 and 1.
	 * 
	 * @param desiredBuildingsPercent
	 *            the desired percentage of buildings.
	 * @param desiredCombatUnitsPercent
	 *            the desired percentage of combat Units.
	 * @param desiredTechs
	 *            the TechTypes that the manager should research.
	 * @param desiredUpgrades
	 *            the UpgradeTypes the manager should pursue.
	 * @param currentWorkerPercent
	 *            the actual current percentage of worker Units.
	 * @param currentBuildingsPercent
	 *            the actual current percentage of buildings.
	 * @param currentCombatUnitsPercent
	 *            the actual current percentage of combat Units.
	 * @param currentUnits
	 *            all current Units.
	 * @param currentTechs
	 *            all currently researched TechTypes of the desired ones.
	 * @param currentUpgrades
	 *            all currently performed UpgradeTypes of the desired ones.
	 * @return a score based on the provided information and the area the
	 *         GameState is performing in.
	 */
	protected abstract double generateScore(double desiredBuildingsPercent,
			double desiredCombatUnitsPercent, HashSet<TechType> desiredTechs,
			HashMap<UpgradeType, Integer> desiredUpgrades, double currentWorkerPercent, double currentBuildingsPercent,
			double currentCombatUnitsPercent, HashMap<UnitType, Integer> currentUnits, HashSet<TechType> currentTechs,
			HashMap<UpgradeType, Integer> currentUpgrades);

	// ------------------------------ Getter / Setter

	public double getCurrentScore() {
		return currentScore;
	}

}
