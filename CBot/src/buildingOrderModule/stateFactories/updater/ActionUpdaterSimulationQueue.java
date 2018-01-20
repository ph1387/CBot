package buildingOrderModule.stateFactories.updater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoringAction;
import buildingOrderModule.scoringDirector.ScoringDirector;
import buildingOrderModule.simulator.ActionType;
import buildingOrderModule.simulator.SimulationStarter;
import buildingOrderModule.simulator.TypeWrapper;
import buildingOrderModule.stateFactories.actions.AvailableActionsSimulationQueue;
import buildingOrderModule.stateFactories.actions.executableActions.ManagerBaseAction;
import buildingOrderModule.stateFactories.actions.executableActions.actionQueues.ActionQueueSimulationResults;
import bwapi.Player;
import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;
import core.CBot;
import core.Core;
import informationStorage.InformationStorage;

/**
 * ActionUpdaterSimulationQueue.java --- Updater for updating a
 * {@link AvailableActionsSimulationQueue} instance.
 * 
 * @author P H - 16.07.2017
 *
 */
public abstract class ActionUpdaterSimulationQueue extends ActionUpdaterGeneral {

	// The Action that is going to be updated. The reference is stored here
	// since the Updater accesses it in multiple locations.
	protected ActionQueueSimulationResults actionQueueSimulationResults;

	// The actions which are used in the simulation.
	private HashSet<ActionType> actionTypes;
	// Used for generating a score for each action used in the simulation. These
	// scores must be updated to represent a valid state of the game for the
	// simulator.
	private ScoringDirector scoringDirector = this.defineScoringDirector();
	// The actions that the ScoringDirector will be updating.
	private HashSet<ScoringAction> scoringActions;

	// Information that change each iteration:
	// All Types plus their amount that are currently produced and inside the
	// action Queue.
	protected HashMap<TypeWrapper, Integer> simulationQueueResultActionTypes;
	// The types that are currently inside the InformationStorage Queues
	protected HashMap<TypeWrapper, Integer> informationStorageQueuesActionTypes;

	// Simulation frequency:
	// The max difference of the index and the size of the action Queue. When
	// the difference is less or equal this value a new simulation is
	// started.
	private int maxActionQueueIndexOffsetTilEnd = 0;
	// Time stamp of the last check of the action Queue.
	private Integer lastSimulationTimeStampFrames = null;
	// Time difference between the checking if the action Queue was being worked
	// on.
	private int nextSimulationTimeStampDifferenceFrames = 300;

	private SimulationStarter simulationActionStarter = new SimulationStarter();

	public ActionUpdaterSimulationQueue(BuildActionManager buildActionManager) {
		super(buildActionManager);
	}

	// -------------------- Functions

	/**
	 * Function for defining which ScoringDirector should be used.
	 * 
	 * @return the ScoringDirector which will be used for updating the scores of
	 *         all Actions used in the simulations.
	 */
	protected abstract ScoringDirector defineScoringDirector();

	@Override
	public void update(BuildActionManager manager) {
		// The initialization can not be done beforehand -> Safety reasons due
		// to the order of possible initializations.
		if (this.actionQueueSimulationResults == null) {
			this.actionQueueSimulationResults = (ActionQueueSimulationResults) this
					.getActionFromInstance(ActionQueueSimulationResults.class);
		}
		if (this.actionTypes == null && this.scoringActions == null) {
			this.updateActionTypesAndScoringActions();
		}

		this.updateSimulationStarter(manager);
	}

	/**
	 * Function for updating the {@link #actionTypes} and the
	 * {@link #scoringActions} as the latter depend on the former ones. This
	 * function first extracts all information from the different Queues
	 * (training, building, etc.) (->
	 * {@link #informationStorageQueuesActionTypes}) as well as the previous
	 * simulation results (-> {@link #simulationQueueResultActionTypes}). Then
	 * the {@link #generateAllAvailableActionTypes(BuildActionManager)} function
	 * is called from the implementing subclass. The order must be kept due to
	 * the latter one depending on the different Queue results since some
	 * actions must not be taken whilst another one is already queued (I.e.: It
	 * is not advised / allowed for to center buildings to be queued together in
	 * order to prevent errors).
	 */
	private void updateActionTypesAndScoringActions() {
		// Get all the Types plus their amount that are currently inside the
		// action Queue (Result of a simulation). Used to prevent i.e. building
		// two centers.
		this.simulationQueueResultActionTypes = this.extractAllSimulationResultActions();

		// Also extract the types that are currently inside the
		// InformationStorage Queues:
		this.informationStorageQueuesActionTypes = this.extractAllForwardedTypes();

		this.actionTypes = this.generateAllAvailableActionTypes(this.buildActionManager);
		this.scoringActions = this.transformAvailableActionsIntoScoringActions();
	}

	/**
	 * Function for extracting all {@link TypeWrapper}s from the
	 * {@link #actionQueueSimulationResults}. These actions represent the
	 * results of the previous simulation that are still stored but not yet
	 * forwarded to the UnitControlModule / queued inside the training- /
	 * building- / addon- / research- / upgrade-Queue.
	 * 
	 * @return a HashMap containing all TypeWrappers that are produced in the
	 *         action Queue of the actionQueueSimulationResults action. </br>
	 *         Key: The produced TypeWrapper.</br>
	 *         Value: The number of times it was found inside the action Queue.
	 */
	protected HashMap<TypeWrapper, Integer> extractAllSimulationResultActions() {
		HashMap<TypeWrapper, Integer> usedActionTypes = new HashMap<TypeWrapper, Integer>();

		if (this.actionQueueSimulationResults != null) {
			for (ActionType actionType : this.actionQueueSimulationResults.getActionQueue()) {
				addToTypeWrapperHashMap(usedActionTypes, actionType.defineResultType());
			}
		}

		return usedActionTypes;
	}

	/**
	 * Function for extracting all types that got forwarded into the
	 * InformationStorage's Queues and convert them to their TypeWrapper
	 * equivalent.
	 * 
	 * @return a HashMap containing all the types that already got forwarded to
	 *         the InformationStorage.</br>
	 *         Key: The forwarded type as TypeWrapper.</br>
	 *         Value: The number of times it was found inside the Queues.
	 */
	protected HashMap<TypeWrapper, Integer> extractAllForwardedTypes() {
		HashMap<TypeWrapper, Integer> forwardedActionTypes = new HashMap<TypeWrapper, Integer>();

		// Building Queue:
		for (UnitType unitType : CBot.getInstance().getWorkerManagerConstructionJobDistribution().getBuildingQueue()) {
			addToTypeWrapperHashMap(forwardedActionTypes, TypeWrapper.generateFrom(unitType));
		}
		// Training Queue:
		for (UnitType unitType : this.buildActionManager.getInformationStorage().getTrainingQueue()) {
			addToTypeWrapperHashMap(forwardedActionTypes, TypeWrapper.generateFrom(unitType));
		}
		// Upgrade Queue:
		for (UpgradeType upgradeType : this.buildActionManager.getInformationStorage().getUpgradeQueue()) {
			addToTypeWrapperHashMap(forwardedActionTypes, TypeWrapper.generateFrom(upgradeType));
		}
		// Research Queue:
		for (TechType techType : this.buildActionManager.getInformationStorage().getResearchQueue()) {
			addToTypeWrapperHashMap(forwardedActionTypes, TypeWrapper.generateFrom(techType));
		}
		// Addon Queue:
		for (UnitType unitType : this.buildActionManager.getInformationStorage().getAddonQueue()) {
			addToTypeWrapperHashMap(forwardedActionTypes, TypeWrapper.generateFrom(unitType));
		}

		return forwardedActionTypes;
	}

	/**
	 * Function for adding a TypeWrapper to a HashMap. If the HashMap does not
	 * contain any previous instance of the TypeWrapper, instantiate it with 1.
	 * Otherwise add 1 to the existing value.
	 * 
	 * @param hashMap
	 *            the HashMap to which a TypeWrapper instance will be counted
	 *            towards.
	 * @param wrapper
	 *            the TypeWrapper that will be added towards a HashMap.
	 */
	private static void addToTypeWrapperHashMap(HashMap<TypeWrapper, Integer> hashMap, TypeWrapper wrapper) {
		if (hashMap.containsKey(wrapper)) {
			hashMap.put(wrapper, hashMap.get(wrapper) + 1);
		} else {
			hashMap.put(wrapper, 1);
		}
	}

	/**
	 * Function for generating all the available ActionTypes that will be used
	 * inside the different simulations.
	 * 
	 * @param buildActionManager
	 *            the manager whose actions are being extracted.
	 * @return a HashSet containing all available ActionTypes for simulations.
	 */
	protected abstract HashSet<ActionType> generateAllAvailableActionTypes(BuildActionManager buildActionManager);

	/**
	 * Function for transforming the available actions into ScoringActions that
	 * can be used by the ScoringDirector.
	 * 
	 * @return a HashSet containing all available ScoringActions for the
	 *         ScoringDirector to be updated.
	 */
	private HashSet<ScoringAction> transformAvailableActionsIntoScoringActions() {
		HashSet<ScoringAction> transformedScoringActions = new HashSet<>();

		for (ActionType actionType : this.actionTypes) {
			if (actionType instanceof ScoringAction) {
				transformedScoringActions.add((ScoringAction) actionType);
			}
		}
		return transformedScoringActions;
	}

	/**
	 * Function for updating all information regarding the SimulationStarter.
	 * Either resetting existing action Queues of the
	 * {@link ActionQueueSimulationResults} instance itself when no progress is
	 * being made (fast enough) or starting a new simulation when the existing
	 * action Queue is near its end and a new Queue is needed to further
	 * progress into the game.
	 * 
	 * @param manager
	 *            the BuildActionManager whose properties / Actions are going to
	 *            be updated.
	 */
	private void updateSimulationStarter(BuildActionManager manager) {
		// Check in predefined time differences if any changes in the action
		// Queues index occurred. If this is not the case the Bot is unable to
		// execute the Actions defined in it and therefore the action Queue must
		// be reseted.
		if (this.lastSimulationTimeStampFrames == null || Core.getInstance().getGame().getFrameCount()
				- this.lastSimulationTimeStampFrames >= this.nextSimulationTimeStampDifferenceFrames) {
			this.lastSimulationTimeStampFrames = Core.getInstance().getGame().getFrameCount();

			// If changes occurred reset the flag.
			if (this.actionQueueSimulationResults.didChangesOccurr()) {
				this.actionQueueSimulationResults.resetChangesFlag();
			}
			// If no changes occurred first set the index to the maximum
			// possible index and then call the reset function. This removes all
			// stored actions and resets the index to 0.
			else {
				this.actionQueueSimulationResults.setIndex(this.actionQueueSimulationResults.getActionQueue().size());
				this.actionQueueSimulationResults.reset();
			}
		}

		if (this.isActionQueueNearlyFinished()) {
			this.performNextSimulation(manager);
		}
	}

	/**
	 * Function for testing if the index of the action Queue of the
	 * ActionQueueSimulationResults is nearly at the end of the action Queue.
	 * 
	 * @return true if the max difference of the index and the size of the
	 *         action Queue is being met (Else false).
	 */
	private boolean isActionQueueNearlyFinished() {
		// The index must nearly be at the end for the function to return true.
		return this.actionQueueSimulationResults.getActionQueue().size()
				- this.actionQueueSimulationResults.getIndex() <= this.maxActionQueueIndexOffsetTilEnd;
	}

	/**
	 * Function for performing another / the next simulation iteration. This
	 * function updates the {@link #actionTypes} as well as the
	 * {@link #scoringActions} with the internal
	 * {@link #updateActionTypesAndScoringActions()} function. Afterwards
	 * another simulation is run.
	 * 
	 * @param manager
	 *            the BuildActionManager whose properties / Actions are going to
	 *            be updated and which is used to access the
	 *            {@link InformationStorage} instance.
	 */
	private void performNextSimulation(BuildActionManager manager) {
		ArrayList<ManagerBaseAction> transformedResult = new ArrayList<>();

		// Extract all currently relevant information.
		int currentFrameTimeStamp = Core.getInstance().getGame().getFrameCount();
		int currentMinerals = Core.getInstance().getPlayer().minerals()
				- manager.getInformationStorage().getResourceReserver().getReservedMinerals();
		int currentGas = Core.getInstance().getPlayer().gas()
				- manager.getInformationStorage().getResourceReserver().getReservedGas();
		UnitType workerUnitType = Core.getInstance().getPlayer().getRace().getWorker();
		List<Unit> units = Core.getInstance().getPlayer().getUnits();

		// Needed since the different Queues must be checked for i.e. duplicates
		// before running another simulation.
		this.updateActionTypesAndScoringActions();

		// Update the scores of the different actions AFTER they were updated!
		// This is due to some actions getting disabled due to i.e. duplicates
		// in some Queue.
		this.scoringDirector.update(this.scoringActions, manager);

		// TODO: DEBUG INFO
		long start = System.nanoTime();

		// The results of the simulation must be converted back into
		// ManagerBaseActions for them to be executable.
		List<ActionType> simulationResult = this.simulationActionStarter.runStarter(this.actionTypes, units,
				currentMinerals, currentGas, workerUnitType, currentFrameTimeStamp);
		for (ActionType actionType : simulationResult) {
			transformedResult.add((ManagerBaseAction) actionType);
		}

		// Forward the transformed ActionTypes towards the Action itself.
		this.actionQueueSimulationResults.addToActionQueue(transformedResult);
		this.lastSimulationTimeStampFrames = currentFrameTimeStamp;

		// TODO: DEBUG INFO
		System.out
				.println("Simulation + forwarding time: " + ((double) (System.nanoTime() - start) / 1000000) + "ms\n");
	}

	// -------------------- Specific tests

	/**
	 * Function for determining if a technology can be researched or not. The
	 * provided ActionType <b>must</b> result in a TechType. If this is not the
	 * case, the function will fail. The function tests if the Player has
	 * researched the resulting TechType of the provided {@link ActionType} and
	 * if he is currently not researching it. Also other checks are done:
	 * <ul>
	 * <li>Test if the required UnitType (Research facility) exists at least
	 * once.</li>
	 * <li>Test if the {@link ActionType} was not already forwarded or is
	 * currently in the research Queue.</li>
	 * <li>Test if an idling, required research facility exists.</li>
	 * <li>Test if the required UnitType of the stored TechType exists at least
	 * once.</li>
	 * </ul>
	 * 
	 * @param manager
	 *            the manager which provides access to the
	 *            {@link InformationStorage} containing the current Unit counts
	 *            as well as the difference Queues.
	 * @param actionType
	 *            the {@link ActionType} (Resulting in a TechType) that is going
	 *            to be checked.
	 * @return true if all criteria regarding the research of the TechType of
	 *         the provided {@link ActionType} are matched, false if not.
	 */
	protected boolean canResearchTechnology(BuildActionManager manager, ActionType actionType) {
		Player player = Core.getInstance().getPlayer();
		boolean notResearched = !player.hasResearched(actionType.defineResultType().getTechType());
		boolean notResearching = !player.isResearching(actionType.defineResultType().getTechType());

		boolean requiredTypeExist = this.doesRequiredTypeExist(manager, actionType);
		boolean notForwardedOrQueued = !this.wasForwardedOrQueued(actionType);
		boolean idleFacilitiyExists = this.isOneProducingFacilityIdle(manager, actionType);
		boolean requiredUnitExist = this.doesRequiredUnitExist(manager, actionType.defineResultType().getTechType());

		return notResearched && notResearching && requiredTypeExist && notForwardedOrQueued && idleFacilitiyExists
				&& requiredUnitExist;
	}

	/**
	 * Function for determining if a upgrade can be performed or not. The
	 * provided ActionType <b>must</b> result in a UpgradeType. If this is not
	 * the case, the function will fail. The function tests if the Player has
	 * not yet reached the maximum upgrade level of the resulting UpgradeType of
	 * the provided {@link ActionType} and if he is currently not performing it.
	 * Also other checks are done:
	 * <ul>
	 * <li>Test if the required UnitType (Upgrade facility) exists at least
	 * once.</li>
	 * <li>Test if the {@link ActionType} was not already forwarded or is
	 * currently in the upgrade Queue.</li>
	 * <li>Test if an idling, required upgrade facility exists.</li>
	 * <li>Test if the required UnitType of the stored UpgradeType of the
	 * current level exists at least once.</li>
	 * </ul>
	 * 
	 * @param manager
	 *            the manager which provides access to the
	 *            {@link InformationStorage} containing the current Unit counts
	 *            as well as the difference Queues.
	 * @param actionType
	 *            the {@link ActionType} (Resulting in a TechType) that is going
	 *            to be checked.
	 * @return true if all criteria regarding the research of the TechType of
	 *         the provided {@link ActionType} are matched, false if not.
	 */
	protected boolean canUpgrade(BuildActionManager manager, ActionType actionType) {
		Player player = Core.getInstance().getPlayer();
		boolean maxLevelNotReached = player.getUpgradeLevel(actionType.defineResultType().getUpgradeType()) < player
				.getMaxUpgradeLevel(actionType.defineResultType().getUpgradeType());
		boolean notUpgrading = !player.isUpgrading(actionType.defineResultType().getUpgradeType());

		boolean requiredTypeExist = this.doesRequiredTypeExist(manager, actionType);
		boolean notForwardedOrQueued = !this.wasForwardedOrQueued(actionType);
		boolean idleFacilitiyExists = this.isOneProducingFacilityIdle(manager, actionType);
		boolean requiredUnitExist = this.doesRequiredUnitExist(manager, actionType.defineResultType().getUpgradeType());

		return maxLevelNotReached && notUpgrading && requiredTypeExist && notForwardedOrQueued && idleFacilitiyExists
				&& requiredUnitExist;
	}

	/**
	 * Function for testing if the required <b>UnitType</b> of the provided
	 * {@link ActionType} exists at least once. This function will fail if the
	 * required type is not a UnitType!
	 * 
	 * @param manager
	 *            the manager which provides access to the
	 *            {@link InformationStorage} containing the current Unit counts.
	 * @param actionType
	 *            the {@link ActionType} (Resulting in a TechType) that is going
	 *            to be checked.
	 * @return true if the required UnitType exists at least once, false if not.
	 */
	protected boolean doesRequiredTypeExist(BuildActionManager manager, ActionType actionType) {
		return manager.getInformationStorage().getCurrentGameInformation().getCurrentUnitCounts()
				.getOrDefault(actionType.defineRequiredType().getUnitType(), 0) > 0;
	}

	/**
	 * Function for testing if the provided {@link ActionType} is going to be
	 * forwarded or is currently in one of the waiting Queues of the Bot. These
	 * include the building, upgrade, training, research or addon Queue.
	 * 
	 * @param actionType
	 *            the {@link ActionType} (Resulting in a TechType) that is going
	 *            to be checked.
	 * @return true if the {@link ActionType} was forwarded into one of the
	 *         {@link InformationStorage}'s Queues or if it is still going to be
	 *         forwarded to them. Otherwise this function returns false.
	 */
	protected boolean wasForwardedOrQueued(ActionType actionType) {
		return this.simulationQueueResultActionTypes.containsKey(actionType.defineResultType())
				|| this.informationStorageQueuesActionTypes.containsKey(actionType.defineResultType());
	}

	/**
	 * Function for testing if one completed and idle facility whose UnitType
	 * was defined in the provided {@link ActionType} exists. The
	 * {@link ActionType} of this function <b>must</b> define a UnitType as
	 * required type. Otherwise this function returns false.
	 * 
	 * @param manager
	 *            the manager which provides access to the
	 *            {@link InformationStorage} containing the current Units.
	 * @param actionType
	 *            the {@link ActionType} (Resulting in a TechType) that is going
	 *            to be checked.
	 * @return true if one (Or more) idling and completed facilities that are
	 *         defined as the required UnitType of the {@link ActionType}
	 *         exist(s). Otherwise this function returns false, if either the
	 *         required type is not a UnitType or no completed and idling Unit
	 *         is found.
	 */
	protected boolean isOneProducingFacilityIdle(BuildActionManager manager, ActionType actionType) {
		boolean prodcuingFacilityIdles = false;

		// Find a required, idle and completed facility for the ActionType.
		for (Unit unit : manager.getInformationStorage().getCurrentGameInformation().getCurrentUnits()
				.getOrDefault(actionType.defineRequiredType().getUnitType(), new HashSet<Unit>())) {
			if (unit.isIdle() && unit.isCompleted()) {
				prodcuingFacilityIdles = true;

				break;
			}
		}

		return prodcuingFacilityIdles;
	}

	/**
	 * Function for testing if a provided TechType's required UnitType is
	 * present on the map at least once.
	 * 
	 * @param manager
	 *            the manager which provides access to the
	 *            {@link InformationStorage} containing the current Units.
	 * @param techType
	 *            the TechType whose required UnitType is going to be checked.
	 * @return true if the required UnitType of the provided TechType is present
	 *         on the map at least once.
	 */
	protected boolean doesRequiredUnitExist(BuildActionManager manager, TechType techType) {
		boolean exist = true;

		if (techType.requiredUnit() != UnitType.None) {
			exist &= manager.getInformationStorage().getCurrentGameInformation().getCurrentUnitCounts()
					.getOrDefault(techType.requiredUnit(), 0) > 0;
		}

		return exist;
	}

	/**
	 * Function for testing if a provided UpgradeType's required UnitTypes are
	 * present on the map at least once. These are based on the current level of
	 * the UpgradeType and must be matched before upgrading to the next level is
	 * possible.
	 * 
	 * @param manager
	 *            the manager which provides access to the
	 *            {@link InformationStorage} containing the current Units.
	 * @param upgradeType
	 *            the UpgradeType whose required UnitTypes are going to be
	 *            checked.
	 * @return true if the required UnitType of the provided UpgradeType is
	 *         present on the map at least once for the next level.
	 */
	protected boolean doesRequiredUnitExist(BuildActionManager manager, UpgradeType upgradeType) {
		int currentUpgradeLevel = manager.getCurrentGameInformation().getCurrentUpgrades().getOrDefault(upgradeType, 0);
		boolean exist = true;

		// Make sure the required UnitType exists at least once and is NOT None!
		if (currentUpgradeLevel > 0 && upgradeType.whatsRequired(currentUpgradeLevel + 1) != UnitType.None) {
			exist &= manager.getInformationStorage().getCurrentGameInformation().getCurrentUnitCounts()
					.getOrDefault(upgradeType.whatsRequired(currentUpgradeLevel + 1), 0) > 0;
		} else if (upgradeType.whatsRequired() != UnitType.None) {
			exist &= manager.getInformationStorage().getCurrentGameInformation().getCurrentUnitCounts()
					.getOrDefault(upgradeType.whatsRequired(), 0) > 0;
		}

		return exist;
	}

	/**
	 * Function for testing if a provided UnitType's required UnitTypes are
	 * present on the map at least once.
	 * 
	 * @param manager
	 *            the manager which provides access to the
	 *            {@link InformationStorage} containing the current Units.
	 * @param unitType
	 *            the UnitType whose required UnitTypes are going to be checked.
	 * @return true if the required UnitTypes of the provided UnitType are
	 *         present on the map at least once.
	 */
	protected boolean doesRequiredUnitExist(BuildActionManager manager, UnitType unitType) {
		boolean exist = true;

		for (UnitType requiredUnitType : unitType.requiredUnits().keySet()) {
			exist &= unitType.requiredUnits().get(requiredUnitType) <= manager.getInformationStorage()
					.getCurrentGameInformation().getCurrentUnitCounts().getOrDefault(requiredUnitType, 0);
		}

		return exist;
	}

	/**
	 * Function for testing if a provided UnitType's required TechType was
	 * already researched by the Player.
	 * 
	 * @param manager
	 *            the manager which provides access to the
	 *            {@link InformationStorage} containing the current Units.
	 * @param unitType
	 *            the UnitType whose required TechType is going to be checked.
	 * @return if the required TechType of the provided UnitType was already
	 *         researched.
	 */
	protected boolean doesRequiredTechExist(BuildActionManager manager, UnitType unitType) {
		boolean exist = true;

		if (unitType.requiredTech() != TechType.None) {
			exist &= manager.getCurrentGameInformation().getCurrentTechs().contains(unitType.requiredTech());
		}

		return exist;
	}

	// ------------------------------ Getter / Setter

}
