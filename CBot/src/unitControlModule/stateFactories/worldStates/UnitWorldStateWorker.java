package unitControlModule.stateFactories.worldStates;

import javaGOAP.GoapState;

/**
 * UnitWorldStateWorker.java --- WorldState for a worker Unit.
 * 
 * @author P H - 29.03.2017
 *
 */
public class UnitWorldStateWorker extends UnitWorldStateDefault {

	public UnitWorldStateWorker() {
		this.add(new GoapState(1, "gatheringMinerals", false));
		this.add(new GoapState(1, "gatheringGas", false));
		this.add(new GoapState(1, "constructing", false));
		this.add(new GoapState(1, "allowGathering", true));
	}
}
