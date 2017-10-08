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
		this.add(new GoapState(0, "enemyKnown", false));
		this.add(new GoapState(0, "destroyUnit", false));
		this.add(new GoapState(0, "retreatFromUnit", false));
		this.add(new GoapState(0, "unitsInRange", false));
		this.add(new GoapState(0, "allowFighting", true));
		this.add(new GoapState(0, "canMove", true));
		this.add(new GoapState(0, "isScout", true));
		this.add(new GoapState(0, "isCloaked", false));
		this.add(new GoapState(0, "isDecloaked", true));
		this.add(new GoapState(0, "mayCloak", false));
	}
}