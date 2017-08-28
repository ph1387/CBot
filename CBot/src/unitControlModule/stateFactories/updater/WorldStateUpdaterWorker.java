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

	// The maximum distance a worker is allowed travel away from a center
	// building when attacking a enemy Unit. Beyond this it is forbidden to
	// start a new attack Action.
	private int maxPixelAttackDistanceToCenter = 600;
	// The maximum distance at which a worker is allowed to search for mineral
	// spots and refineries. Beyond this distance the worker must first return
	// to his nearest center building before searching around him.
	private int maxPixelResourceSearchDistanceToCenter = 300;

	public WorldStateUpdaterWorker(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	public void update(PlayerUnit playerUnit) {
		super.update(playerUnit);

		// Extract the distance to the closest center building for later use.
		Integer closestCenterDistance = playerUnit.generateClosestCenterDistance();

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

		// Only allow fighting if the worker is either near an enemy as well as
		// a center building or is assigned scouting. This keeps workers from
		// running after retreating enemies that attacked the base.
		// NOTE:
		// Also allow the workers to attack when no center remains (distance ==
		// null).
		if ((closestCenterDistance == null)
				|| (closestCenterDistance != null && closestCenterDistance <= this.maxPixelAttackDistanceToCenter
						&& (this.playerUnit.getClosestEnemyUnitInConfidenceRange()) != null)
				|| (((PlayerUnitWorker) playerUnit).isAssignedToSout())) {
			this.changeWorldStateEffect("allowFighting", true);
		} else {
			this.changeWorldStateEffect("allowFighting", false);
		}

		// Change scouting and gathering properties accordingly.
		if (((PlayerUnitWorker) playerUnit).isAssignedToSout()) {
			this.changeWorldStateEffect("isScout", true);
			this.changeWorldStateEffect("allowGathering", false);
		} else {
			this.changeWorldStateEffect("isScout", false);

			// Only allow gathering if the Unit is near a center building.
			if (closestCenterDistance <= this.maxPixelResourceSearchDistanceToCenter) {
				this.changeWorldStateEffect("allowGathering", true);
			} else {
				this.changeWorldStateEffect("allowGathering", false);
			}
		}

		// Update the carrying states of the worker Unit.
		if (playerUnit.getUnit().isCarryingMinerals()) {
			this.changeWorldStateEffect("isCarryingMinerals", true);
		} else {
			this.changeWorldStateEffect("isCarryingMinerals", false);
		}
		if (playerUnit.getUnit().isCarryingGas()) {
			this.changeWorldStateEffect("isCarryingGas", true);
		} else {
			this.changeWorldStateEffect("isCarryingGas", false);
		}
	}

}
