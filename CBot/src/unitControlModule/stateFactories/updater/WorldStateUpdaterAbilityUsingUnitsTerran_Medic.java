package unitControlModule.stateFactories.updater;

import bwapi.Unit;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * WorldStateUpdaterAbilityUsingUnitsTerran_Medic.java ---WorldState updater for
 * Terran_Medic WorldStates.
 * 
 * @author P H - 27.06.2017
 *
 */
public class WorldStateUpdaterAbilityUsingUnitsTerran_Medic extends WorldStateUpdaterAbilityUsingUnits {

	// TODO: UML ADD
	// Has to be larger than the minimum distance at which the protect Action is
	// finished. This is needed to safely move from one state to another.
	// NOTICE:
	// This value can be set lower than the protect Action since the WorldState
	// is only accounted for in the GoapPlanner and has not to actually apply
	// immediately.
	private static final int RANGE_TO_UNITS = 96;

	public WorldStateUpdaterAbilityUsingUnitsTerran_Medic(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	protected void updateAbilitiyWorldState(PlayerUnit playerUnit) {
		boolean healableUnitNear = false;
		boolean supportableUnitNear = false;

		// Find a Unit around the currently executing one that is missing
		// health and is supportable by the Terran_Medic.
		for (Unit unit : playerUnit.getAllPlayerUnitsInRange(RANGE_TO_UNITS)) {
			// Unit is a Bio-Unit..
			switch (unit.getType().toString()) {
			case "Terran_SCV ":
				supportableUnitNear = true;
				break;
			case "Terran_Marine":
				supportableUnitNear = true;
				break;
			case "Terran_Firebat":
				supportableUnitNear = true;
				break;
			case "Terran_Medic":
				supportableUnitNear = true;
				break;
			case "Terran_Ghost":
				supportableUnitNear = true;
				break;
			}

			// Health missing.
			if (supportableUnitNear && !healableUnitNear && unit.getHitPoints() < unit.getType().maxHitPoints()) {
				healableUnitNear = true;
			}

			if (supportableUnitNear && healableUnitNear) {
				break;
			}
		}

		// Change WorldState based on range to Units that can be healed around
		// this Unit.
		if (healableUnitNear) {
			this.changeWorldStateEffect("isNearHealableUnit", true);
		} else {
			this.changeWorldStateEffect("isNearHealableUnit", false);
		}

		// WorldState changes also apply towards the support of other Units.
		if (supportableUnitNear) {
			this.changeWorldStateEffect("isNearSupportableUnit", true);
		} else {
			this.changeWorldStateEffect("isNearSupportableUnit", false);
		}
	}
}
