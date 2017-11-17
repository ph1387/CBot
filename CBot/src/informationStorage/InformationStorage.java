package informationStorage;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;
import bwta.BaseLocation;
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

	// Training / Building related collections.
	private Queue<UnitType> trainingQueue = new LinkedList<UnitType>();
	private Queue<UnitType> addonQueue = new LinkedList<UnitType>();
	private Queue<UpgradeType> upgradeQueue = new LinkedList<UpgradeType>();
	private Queue<TechType> researchQueue = new LinkedList<TechType>();

	// Scouting.
	private HashMap<BaseLocation, Integer> baselocationsSearched = new HashMap<>();

	// Worker specific stuff.
	private ResourceReserver resourceReserver;
	private WorkerConfiguration workerConfig;

	// Storage class for Terran_Science_Vessel follow actions.
	private ScienceVesselStorage scienceVesselStorage;

	// Tracking information.
	private UnitTrackerInformation trackerInfo;

	// Map information
	private MapInformation mapInfo;

	// Information regarding the Player and the current state of the game.
	private CurrentGameInformation currentGameInformation;
	
	// TODO: UML ADD
	// Shared information between all BaseAction (And subclass) instances.
	private BaseActionSharedInformation baseActionSharedInformation;

	// Configuration information:
	private IBuildingOrderModuleConfig iBuildingOrderModuleConfig;
	private IDisplayConfig iDisplayConfig;
	private IInitConfig iInitConfig;
	private IPlayerUnitConfig iPlayerUnitConfig;
	private IUnitControlModuleConfig iUnitControlModuleConfig;
	private IUnitTrackerModuleConfig iUnitTrackerModuleConfig;

	// TODO: UML CHANGE PARAMS
	public InformationStorage(ResourceReserver resourceReserver, WorkerConfiguration workerConfig,
			ScienceVesselStorage scienceVesselStorage, UnitTrackerInformation trackerInfo, MapInformation mapInfo,
			CurrentGameInformation currentGameInformation, BaseActionSharedInformation baseActionSharedInformation) {
		this.resourceReserver = resourceReserver;
		this.workerConfig = workerConfig;
		this.scienceVesselStorage = scienceVesselStorage;
		this.trackerInfo = trackerInfo;
		this.mapInfo = mapInfo;
		this.currentGameInformation = currentGameInformation;
		this.baseActionSharedInformation = baseActionSharedInformation;

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
		this(new ResourceReserver(), new WorkerConfiguration(), new ScienceVesselStorage(),
				new UnitTrackerInformation(), new MapInformation(), new CurrentGameInformation(), new BaseActionSharedInformation());
	}

	// -------------------- Functions

	// ------------------------------ Getter / Setter

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

	public HashMap<BaseLocation, Integer> getBaselocationsSearched() {
		return baselocationsSearched;
	}

	public ResourceReserver getResourceReserver() {
		return resourceReserver;
	}

	public WorkerConfiguration getWorkerConfig() {
		return workerConfig;
	}

	public ScienceVesselStorage getScienceVesselStorage() {
		return scienceVesselStorage;
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
	
	// TODO: UML ADD
	public BaseActionSharedInformation getBaseActionSharedInformation() {
		return baseActionSharedInformation;
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
