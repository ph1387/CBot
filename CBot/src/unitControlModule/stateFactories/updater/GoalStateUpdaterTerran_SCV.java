package unitControlModule.stateFactories.updater;

import unitControlModule.stateFactories.goals.UnitGoalStateTerran_SCV;
import unitControlModule.unitWrappers.PlayerUnit;
import unitControlModule.unitWrappers.PlayerUnitTerran_SCV;

/**
 * GoalStateUpdaterTerran_SCV.java --- Updater for updating a
 * {@link UnitGoalStateTerran_SCV} instance.
 * 
 * @author P H - 09.10.2017
 *
 */
public class GoalStateUpdaterTerran_SCV extends GoalStateUpdaterWorker {

	public GoalStateUpdaterTerran_SCV(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	public void update(PlayerUnit playerUnit) {
		super.update(playerUnit);

		// Workers designated as combat repairers must follow machine Units in
		// the first place and not i.e. attack enemy Units.
		if (((PlayerUnitTerran_SCV) playerUnit).isCombatEngineer()) {
			this.changeGoalStateImportance("isNearRepairableUnit", 6);
			this.changeGoalStateImportance("destroyUnit", 5);
		} else {
			this.changeGoalStateImportance("isNearRepairableUnit", 1);
			// Do NOT change the destroyUnit importance here since it would
			// override any value set in the superclass!
		}
	}

}
