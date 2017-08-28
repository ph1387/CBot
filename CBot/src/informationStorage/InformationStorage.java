package informationStorage;

import java.util.LinkedList;
import java.util.Queue;

import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;
import informationStorage.config.GameConfig;
import informationStorage.config.IBuildingOrderModuleConfig;
import informationStorage.config.IDisplayConfig;
import informationStorage.config.IInitConfig;
import informationStorage.config.IPlayerUnitConfig;
import informationStorage.config.IUnitControlModuleConfig;
import informationStorage.config.IUnitTrackerModuleConfig;

/**
 * InformationStorage.java --- Class for storing and distributing all kinds of
 * building, upgrade, etc. information.
 * 
 * @author P H - 28.04.2017
 *
 */
public class InformationStorage {

	// The number of elements that are allowed in the building / training queue.
	private int maxConcurrentElements = 2;

	// Training / Building related collections
	private Queue<UnitType> trainingQueue = new LinkedList<UnitType>();
	private Queue<UnitType> addonQueue = new LinkedList<UnitType>();
	private Queue<UpgradeType> upgradeQueue = new LinkedList<UpgradeType>();
	private Queue<TechType> researchQueue = new LinkedList<TechType>();

	// Worker specific stuff
	private ResourceReserver resourceReserver;
	private WorkerConfiguration workerConfig;

	// Tracking information
	private UnitTrackerInformation trackerInfo;

	// Map information
	private MapInformation mapInfo;

	// Information regarding the Player and the current state of the game.
	private CurrentGameInformation currentGameInformation;

	// Configuration information:
	private IBuildingOrderModuleConfig iBuildingOrderModuleConfig;
	private IDisplayConfig iDisplayConfig;
	private IInitConfig iInitConfig;
	private IPlayerUnitConfig iPlayerUnitConfig;
	private IUnitControlModuleConfig iUnitControlModuleConfig;
	private IUnitTrackerModuleConfig iUnitTrackerModuleConfig;

	public InformationStorage(ResourceReserver resourceReserver, WorkerConfiguration workerConfig,
			UnitTrackerInformation trackerInfo, MapInformation mapInfo, CurrentGameInformation currentGameInformation) {
		this.resourceReserver = resourceReserver;
		this.workerConfig = workerConfig;
		this.trackerInfo = trackerInfo;
		this.mapInfo = mapInfo;
		this.currentGameInformation = currentGameInformation;

		// Generate the configuration object:
		GameConfig gameConfig = new GameConfig();
		this.iBuildingOrderModuleConfig = gameConfig;
		this.iDisplayConfig = gameConfig;
		this.iInitConfig = gameConfig;
		this.iPlayerUnitConfig = gameConfig;
		this.iUnitControlModuleConfig = gameConfig;
		this.iUnitTrackerModuleConfig = gameConfig;
	}

	public InformationStorage() {
		this(new ResourceReserver(), new WorkerConfiguration(), new UnitTrackerInformation(), new MapInformation(),
				new CurrentGameInformation());
	}

	// -------------------- Functions

	/**
	 * Function for counting all concurrent actions, that are currently being
	 * stored.
	 * 
	 * @return the number of all currently stored actions for the
	 *         UnitControlModule to take.
	 */
	public int getConcurrentQueuedElementCount() {
		int count = 0;

		count += this.trainingQueue.size();
		count += this.upgradeQueue.size();
		count += this.addonQueue.size();
		count += this.researchQueue.size();
		count += this.workerConfig.getBuildingQueue().size();

		return count;
	}

	/**
	 * Function for retrieving the amount of elements that reside in the
	 * training and building queues.
	 * 
	 * @return the amount of elements inside the training and building queues.
	 */
	public int getTrainingAndBuildingQueueSize() {
		return (this.trainingQueue.size() + this.workerConfig.getBuildingQueue().size());
	}

	// ------------------------------ Getter / Setter

	public int getMaxConcurrentElements() {
		return maxConcurrentElements;
	}

	public Queue<UnitType> getTrainingQueue() {
		return trainingQueue;
	}

	public Queue<UnitType> getAddonQueue() {
		return addonQueue;
	}

	public Queue<UpgradeType> getUpgradeQueue() {
		return upgradeQueue;
	}

	public Queue<TechType> getResearchQueue() {
		return researchQueue;
	}

	public ResourceReserver getResourceReserver() {
		return resourceReserver;
	}

	public WorkerConfiguration getWorkerConfig() {
		return workerConfig;
	}

	public UnitTrackerInformation getTrackerInfo() {
		return trackerInfo;
	}

	public MapInformation getMapInfo() {
		return mapInfo;
	}

	public CurrentGameInformation getCurrentGameInformation() {
		return currentGameInformation;
	}

	// ------------------------------ Config:

	public IBuildingOrderModuleConfig getiBuildingOrderModuleConfig() {
		return iBuildingOrderModuleConfig;
	}

	public IDisplayConfig getiDisplayConfig() {
		return iDisplayConfig;
	}

	public IInitConfig getiInitConfig() {
		return iInitConfig;
	}

	public IPlayerUnitConfig getiPlayerUnitConfig() {
		return iPlayerUnitConfig;
	}

	public IUnitControlModuleConfig getiUnitControlModuleConfig() {
		return iUnitControlModuleConfig;
	}

	public IUnitTrackerModuleConfig getiUnitTrackerModuleConfig() {
		return iUnitTrackerModuleConfig;
	}
}
