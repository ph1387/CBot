package unitControlModule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import bwapi.*;
import cBotBWEventDistributor.CBotBWEventDistributor;
import cBotBWEventDistributor.CBotBWEventListener;
import core.Core;

public class UnitControlModule implements CBotBWEventListener {

	private static UnitControlModule instance;
	private static int WORKER_SCOUTING_TRIGGER = 9;

	private boolean workerOnceAssigned = false;

	private HashSet<Unit> combatUnits = new HashSet<Unit>();
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

	// Function for transferring a single worker to the combat units, so that it
	// is used to scout the enemy base at a certain worker amount.
	private void tryTransferringWorkerToCombatUnits() {
		int workerCount = 0;

		for (Unit unit : Core.getInstance().getPlayer().getUnits()) {
			if (unit.getType().isWorker()) {
				workerCount++;
			}
		}

		if (workerCount >= WORKER_SCOUTING_TRIGGER) {
			for (Unit unit : Core.getInstance().getPlayer().getUnits()) {
				if (unit.isGatheringMinerals() && !this.workerOnceAssigned) {
					this.combatUnits.add(unit);
					this.workerOnceAssigned = true;
					this.dispatchNewSperateUnitEvent(unit);
				}
			}
		}
	}

	// ------------------------------ Getter / Setter

	// -------------------- Eventlisteners

	// ------------------------------ Own CBotBWEventListener
	@Override
	public void onStart() {

	}

	@Override
	public void onFrame() {
		// First scouting unit has to be a worker
		if (this.combatUnits.isEmpty() && !this.workerOnceAssigned) {
			this.tryTransferringWorkerToCombatUnits();
		}
	}

	@Override
	public void onUnitCreate(Unit unit) {

	}

	@Override
	public void onUnitComplete(Unit unit) {

	}

	@Override
	public void onUnitDestroy(Unit unit) {

	}

	// -------------------- Events

	// ------------------------------ Separate a worker from a base
	public synchronized void addSeperateUnitEventListener(SeperateUnitEventListener listener) {
		this.seperateUnitListeners.add(listener);
	}

	public synchronized void removeSeperateUnitEventListener(SeperateUnitEventListener listener) {
		this.seperateUnitListeners.remove(listener);
	}

	private synchronized void dispatchNewSperateUnitEvent(Unit unit) {
		for (Object listener : this.seperateUnitListeners) {
			((SeperateUnitEventListener) listener).onSeperateUnit(unit);

			System.out.println("DISPATCHED");
		}
	}
}
