package buildingOrderModule.scoringDirector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.BiConsumer;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.stateFactories.actions.AvailableActionsSimulationQueue;
import buildingOrderModule.stateFactories.actions.executableActions.actionQueues.ActionQueueSimulationResults;
import buildingOrderModule.stateFactories.updater.ActionUpdaterSimulationQueue;
import bwapi.Player;
import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;
import core.Core;

// TODO: UML ADD
/**
 * ScoringDirector.java --- A director that updates the scores of provided
 * ScoringActions based on the current state of the game in comparison with
 * goals that each subclass of the ScoringDirector defines. Different Actions
 * are based on different types of {@link GameState}s whose scores are directly
 * connected to the game and predefined goals. The score of all
 * {@link GameState}s combined divided by the number of states used is the final
 * score of a ScoringAction. </br>
 * <b>Note:</b></br>
 * The ScoringDirector is used for changing the scores of the Actions used in
 * the {@link ActionQueueSimulationResults}. Therefore any subclass must be
 * carefully chosen due to them defining the desired Unit percentages and the
 * technologies and upgrades that count towards the scores. Also since the
 * Updater ({@link ActionUpdaterSimulationQueue}) for the specific
 * {@link AvailableActionsSimulationQueue} defines the actual abilities of the
 * manager, these goals that are defined here should overlap with the actual
 * possible actions of the manager.
 * 
 * @author P H - 16.07.2017
 *
 */
public abstract class ScoringDirector {

	// Desired values:
	private double desiredWorkerPercent = this.defineDesiredWorkerPercent();
	private double desiredBuildingsPercent = this.defineDesiredBuildingsPercent();
	private double desiredCombatUnitsPercent = this.defineDesiredCombatUnitsPercent();
	private HashSet<TechType> desiredTechs = this.defineDesiredTechnologies();
	private HashMap<UpgradeType, Integer> desiredUpgrades = this.defineDesiredUpgradeTypes();

	// Base multiplier which all Actions use to generate their score.
	private double basePointMultiplier = 1000.;

	// -------------------- Functions

	/**
	 * Function for updating the score of a provided HashSet of ScoringActions.
	 * 
	 * @param updatableActions
	 *            the actions whose scores are going to be updated.
	 * @param manager
	 *            the BuildActionManager whose GameStates are going to be
	 *            updated.
	 */
	public void update(HashSet<ScoringAction> updatableActions, BuildActionManager manager) {
		// Update the extracted GameStates
		this.updateGameStates(this.extractUsedGameStates(updatableActions));

		// Update the scores of the actions.
		this.updateScoringActionScores(updatableActions);
	}

	/**
	 * Function for extracting the used GameStates from a HashSet of
	 * ScoringActions.
	 * 
	 * @param scoringActions
	 *            the actions whose GameStates are being extracted.
	 * @return a HashSet of GameStates that the provided ScoringActions use.
	 */
	private HashSet<GameState> extractUsedGameStates(HashSet<ScoringAction> scoringActions) {
		HashSet<GameState> usedGameStates = new HashSet<>();

		for (ScoringAction scoringAction : scoringActions) {
			usedGameStates.addAll(scoringAction.defineUsedGameStates());
		}

		return usedGameStates;
	}

	/**
	 * Function for updating a HashSet of GameStates based on the current state
	 * of the game. The multipliers of the provided GameStates are set
	 * accordingly to the different areas they belong to. These GameStates can
	 * then be used for generating scores in a simulation.
	 * 
	 * @param usedGameStates
	 *            the HashSet of GameStates that are going to be updated.
	 */
	private void updateGameStates(HashSet<GameState> usedGameStates) {
		// Actual values:
		int unitCountTotal = 0;
		int unitCountWorkers = 0;
		int unitCountBuildings = 0;
		int unitCountCombat = 0;
		double currentWorkerPercent;
		double currentBuildingsPercent;
		double currentCombatUnitsPercent;
		HashMap<UnitType, Integer> currentUnits = new HashMap<>();
		HashSet<TechType> currentTechs = new HashSet<>();
		final HashMap<UpgradeType, Integer> currentUpgrades = new HashMap<>();

		final Player player = Core.getInstance().getPlayer();

		// Extract all necessary information from the current state of the game.
		// This does NOT include any building Queues etc. These factors must be
		// considered elsewhere.
		// Units:
		for (Unit unit : player.getUnits()) {
			UnitType type = unit.getType();

			// Add the Units to a HashMap counting the individual Units
			// themselves.
			if (currentUnits.containsKey(type)) {
				currentUnits.put(type, currentUnits.get(type) + 1);
			} else {
				currentUnits.put(type, 1);
			}

			// Count the different types of Units.
			if (type.isWorker()) {
				unitCountWorkers++;
			} else if (type.isBuilding()) {
				unitCountBuildings++;
			} else {
				unitCountCombat++;
			}
			unitCountTotal++;
		}

		// Technologies:
		for (TechType techType : this.desiredTechs) {
			if (player.hasResearched(techType)) {
				currentTechs.add(techType);
			}
		}

		// Upgrades:
		this.desiredUpgrades.forEach(new BiConsumer<UpgradeType, Integer>() {

			@Override
			public void accept(UpgradeType upgradeType, Integer level) {
				currentUpgrades.put(upgradeType, player.getUpgradeLevel(upgradeType));
			}
		});

		// Calculate the percentage representation of the Units:
		currentWorkerPercent = ((double) (unitCountWorkers)) / ((double) (unitCountTotal));
		currentBuildingsPercent = ((double) (unitCountBuildings)) / ((double) (unitCountTotal));
		currentCombatUnitsPercent = ((double) (unitCountCombat)) / ((double) (unitCountTotal));

		// Using these information update all GameStates that are being
		// provided.
		for (GameState gameState : usedGameStates) {
			gameState.updateMultiplier(this.desiredWorkerPercent, this.desiredBuildingsPercent,
					this.desiredCombatUnitsPercent, this.desiredTechs, this.desiredUpgrades, currentWorkerPercent,
					currentBuildingsPercent, currentCombatUnitsPercent, currentUnits, currentTechs, currentUpgrades);
		}
	}

	/**
	 * Function for specifying the desired percentage of workers that the Bot
	 * should have.
	 * 
	 * @return the percentage of workers the Bot should try to keep.
	 */
	protected abstract double defineDesiredWorkerPercent();

	/**
	 * Function for specifying the desired percentage of buildings that the Bot
	 * should have.
	 * 
	 * @return the percentage of buildings the Bot should try to keep.
	 */
	protected abstract double defineDesiredBuildingsPercent();

	/**
	 * Function for specifying the desired percentage of combat Units that the
	 * Bot should have.
	 * 
	 * @return the percentage of combat Units the Bot should try to keep.
	 */
	protected abstract double defineDesiredCombatUnitsPercent();

	/**
	 * Function for defining the TechTypes that the Bot should research.
	 * 
	 * @return the TechTypes the Bot should research.
	 */
	protected abstract HashSet<TechType> defineDesiredTechnologies();

	/**
	 * Function for defining what UpgradeTypes the Bot should pursue.
	 * 
	 * @return the UpgradeTypes and their desired level that the Bot should
	 *         persue.
	 */
	protected abstract HashMap<UpgradeType, Integer> defineDesiredUpgradeTypes();

	/**
	 * Function for updating the scores of the given ScoringActions.
	 * 
	 * @param updatableActions
	 *            the ScoringActions whose scores are being updated.
	 */
	private void updateScoringActionScores(HashSet<ScoringAction> updatableActions) {
		for (ScoringAction scoringAction : updatableActions) {
			double gameStateCount = scoringAction.defineUsedGameStates().size();
			double gameStateSum = 0.;

			// Prevent errors by dividing by 0.
			if (gameStateCount == 0.) {
				gameStateCount = 1;
			}

			// Get the sum of all used GameStates of the action.
			for (GameState gameState : scoringAction.defineUsedGameStates()) {
				gameStateSum += gameState.getCurrentScore();
			}

			// Divide the total sum by the number of GameStates added together
			// to calculate the average value and set the score of the action to
			// this value.
			scoringAction.setScore((int) (this.basePointMultiplier * (gameStateSum / gameStateCount)));
		}
	}

	// ------------------------------ Getter / Setter

}
