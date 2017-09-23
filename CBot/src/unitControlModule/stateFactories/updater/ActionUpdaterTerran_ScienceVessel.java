package unitControlModule.stateFactories.updater;

import java.util.HashSet;

import bwapi.Unit;
import bwapi.UnitType;
import unitControlModule.stateFactories.actions.AvailableActionsTerran_ScienceVessel;
import unitControlModule.stateFactories.actions.executableActions.FollowActionTerran_ScienceVessel;
import unitControlModule.unitWrappers.PlayerUnit;
import unitControlModule.unitWrappers.PlayerUnitTerran_ScienceVessel;

// TODO: UML ADD
/**
 * ActionUpdaterTerran_ScienceVessel.java --- Updater for updating an
 * {@link AvailableActionsTerran_ScienceVessel} instance.
 * 
 * @author P H - 23.09.2017
 *
 */
public class ActionUpdaterTerran_ScienceVessel extends ActionUpdaterGeneral {

	private boolean initializationMissing = true;

	private FollowActionTerran_ScienceVessel followActionTerran_ScienceVessel;

	public ActionUpdaterTerran_ScienceVessel(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	// TODO: UML ADD
	@Override
	public void update(PlayerUnit playerUnit) {
		// Call super.update() when the Units should react independently to
		// enemy Units and retreat on their own. Init() should not be called
		// then!
		// super.update(playerUnit);

		// Get the references to all used actions.
		if (this.initializationMissing) {
			this.init();
			this.initializationMissing = false;
		}

		this.followActionTerran_ScienceVessel.setTarget(this.getClosestSupportableUnit(playerUnit));
	}

	// TODO: UML ADD
	@Override
	protected void init() {
		super.init();

		this.followActionTerran_ScienceVessel = ((FollowActionTerran_ScienceVessel) this
				.getActionFromInstance(FollowActionTerran_ScienceVessel.class));
	}

	// TODO: UML ADD
	/**
	 * Function for finding the closest Unit whose UnitType matches one of the
	 * listed ones in the {@link PlayerUnitTerran_ScienceVessel}'s List of
	 * supportable UnitTypes. The List defined in this Class determines the
	 * order in which the different UnitTypes are searched through. The
	 * UnitTypes at the beginning of the List are preferred while the ones at
	 * the end are considered last.
	 * 
	 * @param playerUnit
	 *            the Unit that is executing the Action.
	 * @return the closest Unit that the executing Unit can support.
	 */
	private Unit getClosestSupportableUnit(PlayerUnit playerUnit) {
		Unit closestSupportableUnit = null;
		double closestSupportableUnitDistance = 0.;

		// Find the closest Unit whose UnitType matches one of the
		// ones supported by the Terran_Science_Vessel.
		for (UnitType unitType : PlayerUnitTerran_ScienceVessel.getSupportableUnitTypes()) {
			for (Unit unit : playerUnit.getInformationStorage().getCurrentGameInformation().getCurrentUnits()
					.getOrDefault(unitType, new HashSet<Unit>())) {
				double currentDistance = playerUnit.getUnit().getDistance(unit);

				if (closestSupportableUnit == null || currentDistance < closestSupportableUnitDistance) {
					closestSupportableUnit = unit;
					closestSupportableUnitDistance = currentDistance;
				}
			}

			// The order in which the UnitTypes are listed in the ScienceVessel
			// do matter!
			if (closestSupportableUnit != null) {
				break;
			}
		}

		return closestSupportableUnit;
	}

}
