package unitControlModule.stateFactories.updater;

import unitControlModule.stateFactories.worldStates.UnitWorldStateWorker;
import unitControlModule.unitWrappers.PlayerUnit;
import unitControlModule.unitWrappers.PlayerUnitWorker;

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

		// Only allow fighting if the worker is either near an enemy or scouting
		if (this.playerUnit.getClosestEnemyUnitInConfidenceRange() != null
				|| ((PlayerUnitWorker) playerUnit).isAssignedToSout()) {
			this.changeWorldStateEffect("allowFighting", true);
		} else {
			this.changeWorldStateEffect("allowFighting", false);
		}

		// Change scouting and gathering properties accordingly
		if (((PlayerUnitWorker) playerUnit).isAssignedToSout()) {
			this.changeWorldStateEffect("isScout", true);
			this.changeWorldStateEffect("allowGathering", false);
		} else {
			this.changeWorldStateEffect("isScout", false);
			this.changeWorldStateEffect("allowGathering", true);
		}
	}
}
