package unitControlModule;

import java.util.HashSet;
import java.util.concurrent.ConcurrentSkipListSet;

import bwapi.Unit;
import cBotBWEventDistributor.CBotBWEndEventListener;
import cBotBWEventDistributor.CBotBWEventDistributor;
import unitControlModule.goapActionTaking.GoapAgent;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * GoapAIThread.java --- Thread on which all GOAP actions run on. This can be
 * quite CPU intensive which is the reason only the actions are being executed
 * here.
 * 
 * @author P H - 29.01.2017
 *
 */
class GoapAIThread extends Thread implements CBotBWEndEventListener {
	
	private UnitControlModule unitControl;
	private Object monitor;
	private boolean running = true;

	private HashSet<GoapAgent> combatUnitAgents = new HashSet<GoapAgent>();
	
	public GoapAIThread(Object monitor) {
		this.monitor = monitor;
	}

	// -------------------- Functions

	public void run() {
		this.unitControl = UnitControlModule.getInstance();

		CBotBWEventDistributor.getInstance().addEndListener(this);

		try {
			while (this.running) {
				this.checkForNewUnits();
				this.checkForDeadUnits();

				for (GoapAgent agent : this.combatUnitAgents) {
					agent.update();
				}
				
				if(this.running) {
					synchronized (this.monitor) {
						this.monitor.wait();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * New units have to be assigned an own GoapAgent.
	 */
	private void checkForNewUnits() {
		while (!this.unitControl.newCombatUnits.isEmpty()) {
			this.combatUnitAgents.add(new GoapAgent(new PlayerUnit(this.unitControl.newCombatUnits.poll())));
		}
	}

	/**
	 * Units which got destroyed have to be removed from the HashSet they are
	 * stored in to prevent errors.
	 */
	private void checkForDeadUnits() {
		while (!this.unitControl.unitsDead.isEmpty()) {
			Unit unitToRemove = this.unitControl.unitsDead.poll();
			GoapAgent matchingAgent = null;

			for (GoapAgent agent : this.combatUnitAgents) {
				if (((PlayerUnit) agent.getAssignedGoapUnit()).getUnit() == unitToRemove) {
					matchingAgent = agent;

					break;
				}
			}

			if (matchingAgent != null) {
				this.combatUnitAgents.remove(matchingAgent);
			}
		}
	}

	// -------------------- Eventlisteners

	// ------------------------------ End of application
	@Override
	public void onEnd(boolean value) {
		this.running = false;
		this.monitor.notifyAll();
	}
}
