package informationStorage;

import java.util.LinkedList;
import java.util.Queue;

import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;

/**
 * InformationStorage.java --- Class for storing and distributing all kinds of
 * building, upgrade, etc. information.
 * 
 * @author P H - 28.04.2017
 *
 */
public class InformationStorage {

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

	public InformationStorage(ResourceReserver resourceReserver, WorkerConfiguration workerConfig,
			UnitTrackerInformation trackerInfo) {
		this.resourceReserver = resourceReserver;
		this.workerConfig = workerConfig;
		this.trackerInfo = trackerInfo;
	}

	public InformationStorage() {
		this(new ResourceReserver(), new WorkerConfiguration(), new UnitTrackerInformation());
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
}
