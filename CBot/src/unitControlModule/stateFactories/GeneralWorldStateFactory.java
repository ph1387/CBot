package unitControlModule.stateFactories;

import java.util.HashSet;

import javaGOAP.GoapState;
import unitControlModule.stateFactories.worldStates.SimpleUnitWorldState;


/**
 * GeneralWorldStateFactory.java --- A Superclass for a generalized WorldState.
 * 
 * @author P H - 26.02.2017
 *
 */
public abstract class GeneralWorldStateFactory implements StateFactory {

	public GeneralWorldStateFactory() {

	}

	// -------------------- Functions

	@Override
	public HashSet<GoapState> generateWorldState() {
		return new SimpleUnitWorldState();
	}
}
