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
		if(this.playerUnit.getConfidence() >= PlayerUnit.CONFIDENCE_THRESHHOLD) {
			this.changeGoalStateImportance("retreatFromUnit", 1);
		} else {
			this.changeGoalStateImportance("retreatFromUnit", 3);
		}
	}
}
