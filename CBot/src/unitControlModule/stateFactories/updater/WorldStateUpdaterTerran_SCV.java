package unitControlModule.stateFactories.updater;

import unitControlModule.stateFactories.worldStates.UnitWorldStateTerran_SCV;
import unitControlModule.unitWrappers.PlayerUnit;
import unitControlModule.unitWrappers.PlayerUnitTerran_SCV;
import unitControlModule.unitWrappers.PlayerUnitWorker;

// TODO: UML ADD
/**
 * WorldStateUpdaterTerran_SCV.java --- Updater for updating a
 * {@link UnitWorldStateTerran_SCV} instance.
 * 
 * @author P H - 09.10.2017
 *
 */
public class WorldStateUpdaterTerran_SCV extends WorldStateUpdaterWorker {

	public WorldStateUpdaterTerran_SCV(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	// TODO: UML ADD
	@Override
	public void update(PlayerUnit playerUnit) {
		boolean isRepairing = playerUnit.getUnit().isRepairing() || playerUnit.getInformationStorage().getWorkerConfig()
				.getUnitMapperRepair().isMapped(playerUnit.getUnit());
		boolean isFollowing = playerUnit.getUnit().isFollowing() || playerUnit.getInformationStorage().getWorkerConfig()
				.getUnitMapperFollow().isMapped(playerUnit.getUnit());

		super.update(playerUnit);

		this.changeWorldStateEffect("repairing", isRepairing);

		// Prevent the Unit from constructing buildings on the battlefield /
		// while repairing another Unit or when the Unit is marked as combat
		// engineer / scouting Unit.
		if (isRepairing || isFollowing || ((PlayerUnitTerran_SCV) playerUnit).isCombatEngineer()
				|| ((PlayerUnitWorker) playerUnit).isAssignedToSout()) {
			this.changeWorldStateEffect("canConstruct", false);
			this.changeWorldStateEffect("allowGathering", false);
		} else {
			this.changeWorldStateEffect("canConstruct", true);
		}

		this.changeWorldStateEffect("isFollowingUnit", isFollowing);

		// isNearRepairableUnit is not changed due to combat engineers then
		// starting to gather resources since the goal is met eventually!
	}
}
