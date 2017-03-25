package unitControlModule.stateFactories;

import java.util.HashSet;

import javaGOAP.GoapState;
import unitControlModule.stateFactories.updater.WorldStateUpdaterDefault;
import unitControlModule.stateFactories.updater.Updater;
import unitControlModule.stateFactories.worldStates.UnitWorldStateDefault;
import unitControlModule.unitWrappers.PlayerUnit;


/**
 * GeneralWorldStateFactory.java --- A Superclass for a generalized WorldState.
 * 
 * @author P H - 26.02.2017
 *
 */
public abstract class WorldStateFactoryDefault implements StateFactory {

	public WorldStateFactoryDefault() {

	}

	// -------------------- Functions

	@Override
	public HashSet<GoapState> generateWorldState() {
		return new UnitWorldStateDefault();
	}
	
	@Override
	public Updater getMatchingWorldStateUpdater(PlayerUnit playerUnit) {
		return new WorldStateUpdaterDefault(playerUnit);
	}
}
