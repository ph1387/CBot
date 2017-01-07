package unitControlModule;

import java.util.ArrayList;
import java.util.List;

import buildingOrderModule.DistributeBuildingOrdersEventListener;
import bwapi.*;
import cBotBWEventDistributor.CBotBWEventDistributor;
import cBotBWEventDistributor.CBotBWEventListener;
import core.Core;
import unitControlModule.scoutCommandManager.ScoutCommandManager;

public class UnitControlModule implements CBotBWEventListener {

	private static UnitControlModule instance;
	private static int WORKER_SCOUTING_TRIGGER = 9;

	private ScoutCommandManager scoutCommandManager = null;
	private boolean scoutingNeeded = true;

	private List<Unit> combatUnits = new ArrayList<Unit>();
	private List<Object> seperateUnitListeners = new ArrayList<Object>();

	private UnitControlModule() {
		CBotBWEventDistributor.getInstance().addListener(this);
	}

	// -------------------- Functions

	// Singleton function
	public static UnitControlModule getInstance() {
		if (instance == null) {
			instance = new UnitControlModule();
		}
		return instance;
	}

	// -------------------- Eventlisteners

	// ------------------------------ Own CBotBWEventListener
	@Override
	public void onStart() {

	}

	@Override
	public void onFrame() {
		if(this.scoutingNeeded) {
			int workerCount = 0;
			
			// Get the worker count, if it is at a certain point, take a worker from the base and go scouting with it
			for (Unit unit : Core.getInstance().getPlayer().getUnits()) {
				if(unit.getType().isWorker()) {
					workerCount++;
				}
			}
			
			if(workerCount >= this.WORKER_SCOUTING_TRIGGER) {
				for (Unit unit : Core.getInstance().getPlayer().getUnits()) {
					if(unit.isGatheringMinerals() && this.scoutingNeeded) {
						this.scoutCommandManager = new ScoutCommandManager(unit);
						this.scoutCommandManager.generateBaseScoutingList();
						this.scoutingNeeded = false;
						this.dispatchNewSperateUnitEvent(unit);
					}
				}
			}
		}
		
		// Run the scout manager
		if (this.scoutCommandManager != null) {
			this.scoutCommandManager.runCommands();
		}
	}

	@Override
	public void onUnitCreate(Unit unit) {

	}

	@Override
	public void onUnitComplete(Unit unit) {
		// Every time a combat unit is completed, add it to the list of combat
		// units
		if (!unit.getType().isBuilding() && !unit.getType().isWorker()) {
			this.combatUnits.add(unit);
		}
	}

	@Override
	public void onUnitDestroy(Unit unit) {
		// When a unit gets destroyed, test if it was a combat unit of the list
		if (!unit.getType().isBuilding() && !unit.getType().isWorker()) {
			this.combatUnits.remove(unit);
		}
	}
	
	// -------------------- Events

	// ------------------------------ Seperate Unit
	public synchronized void addSeperateUnitEventListener(SeperateUnitEventListener listener) {
		this.seperateUnitListeners.add(listener);
	}

	public synchronized void removeSeperateUnitEventListener(SeperateUnitEventListener listener) {
		this.seperateUnitListeners.remove(listener);
	}

	private synchronized void dispatchNewSperateUnitEvent(Unit unit) {
		for (Object listener : seperateUnitListeners) {
			((SeperateUnitEventListener) listener).onSeperateUnit(unit);
		}
	}
}
