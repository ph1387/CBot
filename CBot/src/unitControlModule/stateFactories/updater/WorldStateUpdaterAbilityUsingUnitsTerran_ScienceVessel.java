package unitControlModule.stateFactories.updater;

import java.util.HashSet;

import bwapi.Unit;
import bwapi.UnitType;
import unitControlModule.unitWrappers.PlayerUnit;
import unitControlModule.unitWrappers.PlayerUnitTerran_ScienceVessel;

// TODO: UML ADD
/**
 * WorldStateUpdaterAbilityUsingUnitsTerran_ScienceVessel.java --- WorldState
 * updater for Terran_Science_Vessel WorldStates.
 * 
 * @author P H - 22.09.2017
 *
 */
public class WorldStateUpdaterAbilityUsingUnitsTerran_ScienceVessel extends WorldStateUpdaterAbilityUsingUnits {

	public WorldStateUpdaterAbilityUsingUnitsTerran_ScienceVessel(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	protected void updateAbilitiyWorldState(PlayerUnit playerUnit) {
		boolean supportableUnitNear = false;

		// Test if one of the Units on the map whose UnitType is listed as
		// supportable by the Terran_Science_Vessel is in support range.
		for (UnitType unitType : PlayerUnitTerran_ScienceVessel.getSupportableUnitTypes()) {
			for (Unit unit : playerUnit.getInformationStorage().getCurrentGameInformation().getCurrentUnits()
					.getOrDefault(unitType, new HashSet<Unit>())) {
				if (playerUnit.isNearPosition(unit.getPosition(),
						PlayerUnitTerran_ScienceVessel.getSupportPixelDistance())) {
					supportableUnitNear = true;

					break;
				}
			}
		}

		// WorldState changes apply towards the support of other Units.
		this.changeWorldStateEffect("isNearSupportableUnit", supportableUnitNear);

		// WorldState regarding the following of other Units.
		this.changeWorldStateEffect("isFollowingUnit", !this.playerUnit.getInformationStorage()
				.getScienceVesselStorage().isFollowing(this.playerUnit.getUnit()));
	}

}
