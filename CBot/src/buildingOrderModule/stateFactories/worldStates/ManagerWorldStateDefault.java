package buildingOrderModule.stateFactories.worldStates;

import java.util.HashSet;

import javaGOAP.GoapState;

/**
 * ManagerWorldStateDefault.java --- Default WorldState for a
 * BuildActionManager.
 * 
 * @author P H - 28.04.2017
 *
 */
public class ManagerWorldStateDefault extends HashSet<GoapState> {

	public ManagerWorldStateDefault() {
		this.add(new GoapState(0, "unitsNeeded", true));
		this.add(new GoapState(0, "buildingsNeeded", true));
		
		// TODO: ADD MORE WORLD STATES
	}
}
