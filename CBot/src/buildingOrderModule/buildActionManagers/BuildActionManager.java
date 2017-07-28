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
import bwapi.UpgradeType;
import core.Core;
import informationStorage.CurrentGameInformation;
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

	// Desired values:
	private ArrayList<TechType> desiredTechs;
	private LinkedHashMap<UpgradeType, Integer> desiredUpgrades;

	private CommandSender sender;
	private InformationStorage informationStorage;
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

		// Set the reference to the CurrentGameInformation Object from the
		// shared storage instance.
		this.currentGameInformation = this.informationStorage.getCurrentGameInformation();
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
		this.updateCurrentGameInformation();

		try {
			this.worldStateUpdater.update(this);
			this.goalStateUpdater.update(this);
			this.actionUpdater.update(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Function for extracting all current information from the game itself
	 * regarding the upgrades and technologies and updating the information in
	 * the storage instance.
	 * 
	 */
	private void updateCurrentGameInformation() {
		final HashMap<UpgradeType, Integer> currentUpgrades = new HashMap<>();
		final Player player = Core.getInstance().getPlayer();
		HashSet<TechType> currentTechs = new HashSet<>();

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

		// Forward the information to the storage instance.
		this.informationStorage.getCurrentGameInformation().setCurrentTechs(currentTechs);
		this.informationStorage.getCurrentGameInformation().setCurrentUpgrades(currentUpgrades);
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

	public ArrayList<TechType> getDesiredTechs() {
		return desiredTechs;
	}

	public LinkedHashMap<UpgradeType, Integer> getDesiredUpgrades() {
		return desiredUpgrades;
	}

	public CommandSender getSender() {
		return this.sender;
	}

	public InformationStorage getInformationStorage() {
		return informationStorage;
	}

	public CurrentGameInformation getCurrentGameInformation() {
		return currentGameInformation;
	}
}
