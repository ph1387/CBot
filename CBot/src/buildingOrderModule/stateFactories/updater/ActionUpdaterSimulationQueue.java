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
import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;
import core.Core;

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

	// TODO: UML CHANGE 2
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
	 * Function for generating all the available ActionTypes that will be used
	 * inside the different simulations.
	 * 
	 * @param buildActionManager
	 *            the manager whose actions are being extracted.
	 * @return a HashSet containing all available ActionTypes for simulations.
	 */
	protected abstract HashSet<ActionType> generateAllAvailableActionTypes(BuildActionManager buildActionManager);

	/**
	 * Function for defining which ScoringDirector should be used.
	 * 
	 * @return the ScoringDirector which will be used for updating the scores of
	 *         all Actions used in the simulations.
	 */
	protected abstract ScoringDirector defineScoringDirector();

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

	@Override
	public void update(BuildActionManager manager) {
		// The initialization can not be done beforehand -> Safety reasons due
		// to the order of possible initializations.
		if (this.actionQueueSimulationResults == null) {
			this.actionQueueSimulationResults = (ActionQueueSimulationResults) this
					.getActionFromInstance(ActionQueueSimulationResults.class);
		}
		if (this.actionTypes == null && this.scoringActions == null) {
			this.actionTypes = this.generateAllAvailableActionTypes(this.buildActionManager);
			this.scoringActions = this.transformAvailableActionsIntoScoringActions();
		}

		this.updateSimulationStarter(manager);
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

		// Check if the index of the action Queue is nearly at the end of the
		// Queue. If it is, start a new simulation.
		if (this.isActionQueueNearlyFinished()) {
			ArrayList<ManagerBaseAction> transformedResult = new ArrayList<>();

			// Extract all currently relevant information.
			int currentFrameTimeStamp = Core.getInstance().getGame().getFrameCount();
			int currentMinerals = Core.getInstance().getPlayer().minerals();
			int currentGas = Core.getInstance().getPlayer().gas();
			UnitType workerUnitType = Core.getInstance().getPlayer().getRace().getWorker();
			List<Unit> units = Core.getInstance().getPlayer().getUnits();

			// Update the score of all actions being used in the simulation.
			this.actionTypes = this.generateAllAvailableActionTypes(manager);
			this.scoringActions = this.transformAvailableActionsIntoScoringActions();
			this.scoringDirector.update(this.scoringActions, manager);

			// TODO: DEBUG INFO
			long start = System.nanoTime();

			// Transform the ActionTypes back into ManagerBaseActions.
			for (ActionType actionType : this.simulationActionStarter.runStarter(this.actionTypes, units,
					currentMinerals, currentGas, workerUnitType, currentFrameTimeStamp)) {
				transformedResult.add((ManagerBaseAction) actionType);
			}

			// Forward the transformed ActionTypes towards the Action itself.
			this.actionQueueSimulationResults.addToActionQueue(transformedResult);
			this.lastSimulationTimeStampFrames = currentFrameTimeStamp;

			// TODO: DEBUG INFO
			System.out.println(
					"Simulation + forwarding time: " + ((double) (System.nanoTime() - start) / 1000000) + "ms\n");
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
	 * Function for extracting all currently produces Types (TypeWrappers) from
	 * the action Queue.
	 * 
	 * @return a HashMap containing all TypeWrappers that are produced in the
	 *         action Queue of the actionQueueSimulationResults action. </br>
	 *         Key: The produced TypeWrapper.</br>
	 *         Value: The number of times it was found inside the action Queue.
	 */
	protected HashMap<TypeWrapper, Integer> extractAllProducedTypes() {
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

	// ------------------------------ Getter / Setter

}
