package unitControlModule;

import java.util.LinkedList;
import java.util.Queue;

import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;

// TODO: OWN PACKAGE
// TODO: UML
/**
 * InformationPreserver.java --- Class for storing and distributing all kinds of
 * building, upgrade, etc. information.
 * 
 * @author P H - 28.04.2017
 *
 */
public class InformationPreserver {

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

	public InformationPreserver(ResourceReserver resourceReserver, WorkerConfiguration workerConfig, UnitTrackerInformation trackerInfo) {
		this.resourceReserver = resourceReserver;
		this.workerConfig = workerConfig;
		this.trackerInfo = trackerInfo;
	}
	
	public InformationPreserver() {
		this(new ResourceReserver(), new WorkerConfiguration(), new UnitTrackerInformation());
	}
	
	public InformationPreserver(ResourceReserver resourceReserver) {
		this(resourceReserver, new WorkerConfiguration(), new UnitTrackerInformation());
	}
	
	public InformationPreserver(WorkerConfiguration workerConfig) {
		this(new ResourceReserver(), workerConfig, new UnitTrackerInformation());
	}
	
	public InformationPreserver(UnitTrackerInformation trackerInfo) {
		this(new ResourceReserver(), new WorkerConfiguration(), trackerInfo);
	}
	
	public InformationPreserver(WorkerConfiguration workerConfig, UnitTrackerInformation trackerInfo) {
		this(new ResourceReserver(), workerConfig, trackerInfo);
	}
	
	public InformationPreserver(ResourceReserver resourceReserver, UnitTrackerInformation trackerInfo) {
		this(resourceReserver, new WorkerConfiguration(), trackerInfo);
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
