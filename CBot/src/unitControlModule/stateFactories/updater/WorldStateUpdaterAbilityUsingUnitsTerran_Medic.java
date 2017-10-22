package unitControlModule.stateFactories.updater;

import java.util.HashSet;

import bwapi.Unit;
import bwapi.UnitType;
import unitControlModule.unitWrappers.PlayerUnit;
import unitControlModule.unitWrappers.PlayerUnitTerran_Medic;

/**
 * WorldStateUpdaterAbilityUsingUnitsTerran_Medic.java --- WorldState updater
 * for Terran_Medic WorldStates.
 * 
 * @author P H - 27.06.2017
 *
 */
public class WorldStateUpdaterAbilityUsingUnitsTerran_Medic extends WorldStateUpdaterAbilityUsingUnits {

	// TODO: UML REMOVE
	// // Has to be larger than the minimum distance at which the protect Action
	// is
	// // finished. This is needed to safely move from one state to another.
	// // NOTICE:
	// // This value can be set lower than the protect Action since the
	// WorldState
	// // is only accounted for in the GoapPlanner and has not to actually apply
	// // immediately.
	// private static final int RANGE_TO_UNITS = 96;

	public WorldStateUpdaterAbilityUsingUnitsTerran_Medic(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	protected void updateAbilitiyWorldState(PlayerUnit playerUnit) {
		boolean healableUnitNear = false;

		// Find a Unit around the currently executing one that is missing
		// health and is supportable by the Terran_Medic.
		for (UnitType unitType : PlayerUnitTerran_Medic.getHealableUnitTypes()) {
			HashSet<Unit> units = playerUnit.getInformationStorage().getCurrentGameInformation().getCurrentUnits()
					.getOrDefault(unitType, new HashSet<Unit>());

			for (Unit unit : units) {
				boolean isNearUnit = playerUnit.isNearPosition(unit.getPosition(),
						PlayerUnitTerran_Medic.getHealPixelDistance());
				boolean unitIsDamaged = unit.isCompleted() && unit.getHitPoints() < unit.getType().maxHitPoints();

				// Find a Unit (Other than the executing one) that is healable.
				// This excludes the Terran_Medic UnitType since the Medics
				// would otherwise clump together and not move since this world
				// state is used for following Units around.
				if (!healableUnitNear && unit != playerUnit.getUnit() && unitType != UnitType.Terran_Medic & isNearUnit
						&& unitIsDamaged) {
					healableUnitNear = true;
				}
			}
		}

		// Change WorldState based on range to Units that can be healed around
		// this Unit.
		this.changeWorldStateEffect("isNearHealableUnit", healableUnitNear);
	}
}
