package buildingOrderModule.buildActionManagers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Queue;
import java.util.function.BiConsumer;

import buildingOrderModule.CommandSender;
import buildingOrderModule.stateFactories.StateFactory;
import buildingOrderModule.stateFactories.actions.executableActions.ResearchBaseAction;
import buildingOrderModule.stateFactories.actions.executableActions.UpgradeBaseAction;
import buildingOrderModule.stateFactories.updater.Updater;
import bwapi.Player;
import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;
import core.Core;
import informationStorage.InformationStorage;
import javaGOAP.GoapAction;
import javaGOAP.GoapUnit;

/**
 * BuildActionManager.java --- Superclass for all action managers.
 * 
 * @author P H - 28.04.2017
 *
 */
public abstract class BuildActionManager extends GoapUnit {

	// TODO: UML ADD
	// Desired values:
	private ArrayList<TechType> desiredTechs;
	// TODO: UML ADD
	private LinkedHashMap<UpgradeType, Integer> desiredUpgrades;

	private CommandSender sender;
	private InformationStorage informationStorage;
	// TODO: UML ADD
	private CurrentGameInformation currentGameInformation;

	// Factories and Objects needed for an accurate representation of the
	// managers capabilities.
	private StateFactory stateFactory;
	private Updater worldStateUpdater;
	private Updater goalStateUpdater;
	private Updater actionUpdater;

	public BuildActionManager(CommandSender sender, InformationStorage informationStorage) {
		this.sender = sender;
		this.informationStorage = informationStorage;

		this.stateFactory = this.createFactory();
		this.worldStateUpdater = this.stateFactory.getMatchingWorldStateUpdater(this);
		this.goalStateUpdater = this.stateFactory.getMatchingGoalStateUpdater(this);
		this.actionUpdater = this.stateFactory.getMatchingActionUpdater(this);

		this.setWorldState(this.stateFactory.generateWorldState());
		this.setGoalState(this.stateFactory.generateGoalState());
		this.setAvailableActions(this.stateFactory.generateAvailableActions());

		// Extract the desired TechTypes and UpgradeTypes from the available
		// actions.
		this.desiredTechs = this.extractDesiredTechs();
		this.desiredUpgrades = this.extractDesiredUpgrades();
	}

	// -------------------- Functions

	/**
	 * Function for defining the TechTypes that the Bot should research. These
	 * are extracted from the available actions ordered set and are added
	 * towards a separate ArrayList. This is necessary for an easier
	 * understanding of how many TechTypes are going to be researched.
	 * 
	 * @return the TechTypes the Bot should research.
	 */
	private ArrayList<TechType> extractDesiredTechs() {
		ArrayList<TechType> techs = new ArrayList<>();

		// Extract all technologies from the available actions.
		for (GoapAction goapAction : this.getAvailableActions()) {
			if (goapAction instanceof ResearchBaseAction) {
				techs.add(((ResearchBaseAction) goapAction).defineResultType().getTechType());
			}
		}

		return techs;
	}

	/**
	 * Function for defining what UpgradeTypes the Bot should pursue. These are
	 * extracted from the available actions and are set to the maximum number of
	 * iterations the specific UpgradeType can be performed. This is necessary
	 * for an easier understanding of how many UpgradeTypes are going to be
	 * pursued.
	 * 
	 * @return the UpgradeTypes and their desired level that the Bot should
	 *         pursue.
	 */
	private LinkedHashMap<UpgradeType, Integer> extractDesiredUpgrades() {
		LinkedHashMap<UpgradeType, Integer> upgrades = new LinkedHashMap<>();

		// Extract all upgrades from the available actions and set the desired
		// rank to the maximum one.
		for (GoapAction goapAction : this.getAvailableActions()) {
			if (goapAction instanceof UpgradeBaseAction) {
				UpgradeType upgradeType = ((UpgradeBaseAction) goapAction).defineResultType().getUpgradeType();

				upgrades.put(upgradeType, upgradeType.maxRepeats());
			}
		}

		return upgrades;
	}

	@Override
	public void update() {
		// Extract the current information of the game.
		this.currentGameInformation = this.extractCurrentGameInformation();

		try {
			this.worldStateUpdater.update(this);
			this.goalStateUpdater.update(this);
			this.actionUpdater.update(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// TODO: UML ADD
	/**
	 * Function for extracting all current information from the game itself.
	 * 
	 * @return a Object that holds all important game information.
	 */
	private CurrentGameInformation extractCurrentGameInformation() {
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

		return new CurrentGameInformation(unitCountWorkers, unitCountBuildings, unitCountCombat, currentWorkerPercent,
				currentBuildingsPercent, currentCombatUnitsPercent, currentUnits, currentTechs, currentUpgrades);
	}

	/**
	 * Function for providing a StateFactory which will be used to determine the
	 * possible actions, goals, worldState etc. of the BuildActionManager.
	 * 
	 * @return a StateFactory providing all necessary information.
	 */
	protected abstract StateFactory createFactory();

	@Override
	public void goapPlanFailed(Queue<GoapAction> actionQueue) {

	}

	@Override
	public void goapPlanFinished() {

	}

	@Override
	public void goapPlanFound(Queue<GoapAction> actionQueue) {

	}

	@Override
	public boolean moveTo(Object target) {
		// Can not move, but if this is mistakenly called, it should throw an
		// Exception and continue.
		try {
			throw new Exception("Move to function of the Building Unit called!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		return true;
	}

	// ------------------------------ Getter / Setter

	// TODO: UML ADD
	public ArrayList<TechType> getDesiredTechs() {
		return desiredTechs;
	}

	// TODO: UML ADD
	public LinkedHashMap<UpgradeType, Integer> getDesiredUpgrades() {
		return desiredUpgrades;
	}

	public CommandSender getSender() {
		return this.sender;
	}

	public InformationStorage getInformationStorage() {
		return informationStorage;
	}

	// TODO: UML ADD
	public CurrentGameInformation getCurrentGameInformation() {
		return currentGameInformation;
	}
}
