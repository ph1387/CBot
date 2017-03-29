package unitControlModule.stateFactories.worldStates;

import javaGOAP.GoapState;
//TODO: UML
/**
 * UnitWorldStateWorker.java --- WorldState for a worker Unit.
 * 
 * @author P H - 29.03.2017
 *
 */
public class UnitWorldStateWorker extends UnitWorldStateDefault {

	public UnitWorldStateWorker() {
		this.add(new GoapState(0, "gatheringMinerals", false));
		this.add(new GoapState(0, "gatheringGas", false));
		this.add(new GoapState(0, "constructing", false));
	}
}
