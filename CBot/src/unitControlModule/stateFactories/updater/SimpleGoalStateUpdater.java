package unitControlModule.stateFactories.updater;

import unitControlModule.stateFactories.goals.SimpleUnitGoalState;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * SimpleGoalStateUpdater.java --- Updater for updating a
 * {@link SimpleUnitGoalState} instance.
 * 
 * @author P H - 26.02.2017
 *
 */
public class SimpleGoalStateUpdater extends GeneralGoalStateUpdater {

	public SimpleGoalStateUpdater(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	public void update(PlayerUnit playerUnit) {
		if(this.playerUnit.confidence >= PlayerUnit.CONFIDENCE_THRESHHOLD) {
			this.changeGoalStateImportance("retreatFromUnit", 1);
		} else {
			this.changeGoalStateImportance("retreatFromUnit", 3);
		}
	}
}
