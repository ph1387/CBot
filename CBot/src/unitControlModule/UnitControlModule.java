package unitControlModule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;

import bwapi.*;
import cBotBWEventDistributor.CBotBWEventDistributor;
import cBotBWEventDistributor.CBotBWEventListener;
import core.Core;
import unitControlModule.goapActionTaking.GoapAgent;

/**
 * UnitControlModule.java --- Module for controlling the player units
 * 
 * @author P H - 29.01.2017
 *
 */
public class UnitControlModule implements CBotBWEventListener {

	private static UnitControlModule instance;
	private static int WORKER_SCOUTING_TRIGGER = 9;

	private boolean workerOnceAssigned = false;
	boolean aiThreadRunning = true;

	// Units the AIThread is going to assign to GoapAgents / removes from them.
	ConcurrentLinkedQueue<Unit> newCombatUnits = new ConcurrentLinkedQueue<Unit>();
	ConcurrentLinkedQueue<Unit> unitsDead = new ConcurrentLinkedQueue<Unit>();

	private List<Object> seperateUnitListeners = new ArrayList<Object>();

	private UnitControlModule() {
		GoapAIThread aiThread = new GoapAIThread();

		aiThread.start();

		CBotBWEventDistributor.getInstance().addListener(this);
	}

	// -------------------- Functions

	/**
	 * Singleton function.
	 * 
	 * @return instance of the class.
	 */
	public static UnitControlModule getInstance() {
		if (instance == null) {
			instance = new UnitControlModule();
		}
		return instance;
	}

	// TODO: Implementation: tryTransferringWorkerToCombatUnits
	/**
	 * Function for transferring a single worker to the combat units, so that it
	 * is used to scout the enemy base at a certain worker amount.
	 */
	private void tryTransferringWorkerToCombatUnits() {
		int workerCount = 0;

		for (Unit unit : Core.getInstance().getPlayer().getUnits()) {
			if (unit.getType().isWorker()) {
				workerCount++;
			}
		}

		if (workerCount >= WORKER_SCOUTING_TRIGGER) {
			for (Unit unit : Core.getInstance().getPlayer().getUnits()) {

				// TODO: REMOVE
				System.out.println("UNIT: " + unit + " " + unit.getType() + " " + unit.getType().isWorker() + " "
						+ unit.isGatheringMinerals() + " " + unit.isCompleted() + " " + this.workerOnceAssigned);
				// Unit references are still not the same

				if (unit.getType().isWorker() && unit.isGatheringMinerals() && unit.isCompleted()
						&& !this.workerOnceAssigned) {

					// TODO: REMOVE
					System.out.println("CHOSEN: " + unit);

					this.dispatchNewSperateUnitEvent(unit);
					// TODO: References are not equal
					// this.newCombatUnits.add(unit);
					this.workerOnceAssigned = true;
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

		// TODO: Unit references do not match!
		// Also Nullpointer at units target! this.action == null in actions
		// -> Be careful!

		// First scouting unit has to be a worker
		// if (!this.workerOnceAssigned) {
		// this.tryTransferringWorkerToCombatUnits();
		// }
	}

	@Override
	public void onUnitCreate(Unit unit) {

	}

	/**
	 * OnUnitComplete does also trigger for enemy units!
	 * 
	 * @see cBotBWEventDistributor.CBotBWEventListener#onUnitComplete(bwapi.Unit)
	 */
	@Override
	public void onUnitComplete(Unit unit) {
		if (unit.getPlayer() == Core.getInstance().getPlayer() && !unit.getType().isBuilding()
				&& !unit.getType().isWorker()) {
			this.newCombatUnits.add(unit);
		}
	}

	@Override
	public void onUnitDestroy(Unit unit) {
		if (!unit.getType().isBuilding() && !unit.getType().isWorker()) {
			this.unitsDead.add(unit);
		}
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

			// TODO: REMOVE System.out
			System.out.println("Tried to seperate worker.");
		}
	}
}
