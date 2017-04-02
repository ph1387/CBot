package unitControlModule.stateFactories.updater;

import unitControlModule.stateFactories.worldStates.UnitWorldStateWorker;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * WorldStateUpdaterWorker.java --- Updater for updating a
 * {@link UnitWorldStateWorker} instance.
 * 
 * @author P H - 29.03.2017
 *
 */
public class WorldStateUpdaterWorker extends WorldStateUpdaterDefault {

	public WorldStateUpdaterWorker(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	public void update(PlayerUnit playerUnit) {
		super.update(playerUnit);

		if (playerUnit.getUnit().isGatheringMinerals()) {
			this.changeWorldStateEffect("gatheringMinerals", true);
		} else {
			this.changeWorldStateEffect("gatheringMinerals", false);
		}

		if (playerUnit.getUnit().isGatheringGas()) {
			this.changeWorldStateEffect("gatheringGas", true);
		} else {
			this.changeWorldStateEffect("gatheringGas", false);
		}

		if (playerUnit.getUnit().isConstructing()) {
			this.changeWorldStateEffect("constructing", true);
		} else {
			this.changeWorldStateEffect("constructing", false);
		}
	}
}
