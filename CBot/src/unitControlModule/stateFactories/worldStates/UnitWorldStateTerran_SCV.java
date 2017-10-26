package unitControlModule.stateFactories.worldStates;

import javaGOAP.GoapState;

/**
 * UnitWorldStateTerran_SCV.java --- WorldState for a Terran_SCV. This Class
 * exists due to them being able repair certain Units / buildings.
 * 
 * @author P H - 09.10.2017
 *
 */
public class UnitWorldStateTerran_SCV extends UnitWorldStateWorker {

	public UnitWorldStateTerran_SCV() {
		this.add(new GoapState(0, "repairing", false));
		this.add(new GoapState(0, "isFollowingUnit", false));
		this.add(new GoapState(0, "isNearRepairableUnit", false));
	}

}
