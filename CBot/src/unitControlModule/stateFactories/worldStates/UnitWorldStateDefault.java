package unitControlModule.stateFactories.worldStates;

import java.util.HashSet;

import javaGOAP.GoapState;

/**
 * SimpleUnitWorldState.java --- A simple WorldState for a Unit containing all
 * basic states.
 * 
 * @author P H - 26.02.2017
 */
public class UnitWorldStateDefault extends HashSet<GoapState> {

	public UnitWorldStateDefault() {
		this.add(new GoapState(1, "enemyKnown", false));
		this.add(new GoapState(1, "destroyUnit", false));
		this.add(new GoapState(1, "retreatFromUnit", false));
		this.add(new GoapState(1, "unitsInRange", false));
		this.add(new GoapState(1, "unitsInSight", false));
		this.add(new GoapState(1, "isScout", true));
		this.add(new GoapState(1, "allowFighting", true));
		this.add(new GoapState(1, "canMove", true));
	}
}
