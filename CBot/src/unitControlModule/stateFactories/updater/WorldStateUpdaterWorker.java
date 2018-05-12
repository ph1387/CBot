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
	// TODO: UML ADD
	// The minimum number of workers that must be gathering minerals before any
	// are assigned to refineries.
	private int minWorkerCountGatheringMinerals = 10;

	public WorldStateUpdaterWorker(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	public void update(PlayerUnit playerUnit) {
		super.update(playerUnit);

		// Extract the distance to the closest center building for later use.
		Integer closestCenterDistance = playerUnit.generateClosestCenterDistance();

		// Ensure that always enough workers are gathering minerals.
		boolean enoughMineralGatherers = playerUnit.getInformationStorage().getCurrentGameInformation()
				.getCurrentMineralGatherers() >= this.minWorkerCountGatheringMinerals;
		this.changeWorldStateEffect("allowGatheringGas", enoughMineralGatherers);

		this.changeWorldStateEffect("gatheringMinerals", playerUnit.getUnit().isGatheringMinerals());
		this.changeWorldStateEffect("gatheringGas", playerUnit.getUnit().isGatheringGas());
		this.changeWorldStateEffect("constructing", playerUnit.getUnit().isConstructing());
		this.changeWorldStateEffect("isCarryingMinerals", playerUnit.getUnit().isCarryingMinerals());
		this.changeWorldStateEffect("isCarryingGas", playerUnit.getUnit().isCarryingGas());

		// Only allow fighting if the worker is either near an enemy as well as
		// a center building or is assigned scouting. This keeps workers from
		// running after retreating enemies that attacked the base.
		// NOTE:
		// Also allow the workers to attack when no center remains (distance ==
		// null).
		if ((closestCenterDistance == null)
				|| (closestCenterDistance != null && closestCenterDistance <= this.maxPixelAttackDistanceToCenter
						&& (playerUnit.getAttackableEnemyUnitToReactTo()) != null)
				|| (((PlayerUnitWorker) playerUnit).isAssignedToSout())) {
			this.changeWorldStateEffect("allowFighting", true);
		} else {
			this.changeWorldStateEffect("allowFighting", false);
		}

		// Change scouting and gathering properties accordingly.
		if (((PlayerUnitWorker) playerUnit).isAssignedToSout()) {
			this.changeWorldStateEffect("isScout", true);
			this.changeWorldStateEffect("canConstruct", false);
			this.changeWorldStateEffect("allowGathering", false);
			this.changeWorldStateEffect("isNearCenter", false);
		} else {
			this.changeWorldStateEffect("isScout", false);
			this.changeWorldStateEffect("canConstruct", true);

			// Only allow gathering if the Unit is near a center building.
			this.changeWorldStateEffect("allowGathering", true);
			this.changeWorldStateEffect("isNearCenter", closestCenterDistance != null
					&& closestCenterDistance <= this.maxPixelResourceSearchDistanceToCenter);
		}
	}

}
