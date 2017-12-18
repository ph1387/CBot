package unitControlModule.stateFactories.updater;

import unitControlModule.stateFactories.goals.UnitGoalStateDefault;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * SimpleGoalStateUpdater.java --- Updater for updating a
 * {@link UnitGoalStateDefault} instance.
 * 
 * @author P H - 26.02.2017
 *
 */
public class GoalStateUpdaterDefault extends GoalStateUpdaterGeneral {

	public GoalStateUpdaterDefault(PlayerUnit playerUnit) {
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

		// Needs to be changed to ensure that the Units move together.
		if (playerUnit.needsGrouping()) {
			this.changeGoalStateImportance("grouped", 3);
		} else {
			this.changeGoalStateImportance("grouped", 0);
		}
	}
}
