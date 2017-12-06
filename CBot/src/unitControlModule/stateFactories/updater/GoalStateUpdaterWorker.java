package unitControlModule.stateFactories.updater;

import unitControlModule.stateFactories.goals.UnitGoalStateWorker;
import unitControlModule.unitWrappers.PlayerUnit;
import unitControlModule.unitWrappers.PlayerUnitWorker;

/**
 * GoalStateUpdaterWorker.java --- Updater for updating a
 * {@link UnitGoalStateWorker} instance.
 * 
 * @author P H - 25.03.2017
 *
 */
public class GoalStateUpdaterWorker extends GoalStateUpdaterGeneral {

	private boolean goalsOnceChanged = false;

	public GoalStateUpdaterWorker(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	public void update(PlayerUnit playerUnit) {
		if (this.playerUnit.isConfidenceAboveThreshold()) {
			this.changeGoalStateImportance("retreatFromUnit", 1);
		} else {
			this.changeGoalStateImportance("retreatFromUnit", 10);
		}

		// Let workers fight if an enemy is near them
		if (this.playerUnit.getAttackableEnemyUnitToReactTo() != null) {
			this.changeGoalStateImportance("destroyUnit", 7);
		} else {
			this.changeGoalStateImportance("destroyUnit", 1);
		}

		// Initiate the scouting in the beginning of the match if certain
		// criteria are matched. This transforms the worker Unit into a basic
		// combat one.
		if (((PlayerUnitWorker) this.playerUnit).isAssignedToSout() && !this.goalsOnceChanged) {
			// Enables the reassigning of other goals to the Unit.
			this.goalsOnceChanged = true;

			this.changeGoalStateImportance("constructing", 0);
			this.changeGoalStateImportance("gatheringGas", 0);
			this.changeGoalStateImportance("gatheringMinerals", 0);

			this.changeGoalStateImportance("enemyKnown", 1);
			this.changeGoalStateImportance("destroyUnit", 2);
			// Retreat stays the same
		}
	}
}
