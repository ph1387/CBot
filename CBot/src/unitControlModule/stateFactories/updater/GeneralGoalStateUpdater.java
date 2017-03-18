package unitControlModule.stateFactories.updater;

import javaGOAP.GoapState;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * GeneralGoalStateUpdater.java --- Superclass for updating most GoalStates.
 * 
 * @author P H - 26.02.2017
 *
 */
public abstract class GeneralGoalStateUpdater implements Updater {

	protected PlayerUnit playerUnit;

	public GeneralGoalStateUpdater(PlayerUnit playerUnit) {
		this.playerUnit = playerUnit;
	}

	// -------------------- Functions

	/**
	 * Convenience function.
	 * 
	 * @param effect
	 *            the effect of the goalState whose importance is going to be
	 *            changed.
	 * @param importance
	 *            the new importance that the goalState is going to be assigned
	 *            to.
	 * @return true or false depending if the goalState is found or not.
	 * 
	 * @see #changeGoalStateImportance(GoapState, int)
	 */
	protected boolean changeGoalStateImportance(String effect, int importance) {
		return this.changeGoalStateImportance(this.getGoalFromEffect(effect), importance);
	}

	/**
	 * Function for changing the importance of a specific goalState.
	 * 
	 * @param state
	 *            the goalState whose importance is going to be changed.
	 * @param importance
	 *            the new importance that the goalState is going to be assigned
	 *            to.
	 * @return true or false depending if the goalState is found or not.
	 */
	protected boolean changeGoalStateImportance(GoapState state, int importance) {
		try {
			state.importance = importance;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Function for retrieving a set goalState from this units goalState List
	 * depending on the effect the goal was assigned.
	 * 
	 * @param effect
	 *            the effect whose goalState is going to be searched for.
	 * @return the goalState that has the given effect or null if none is found.
	 */
	protected GoapState getGoalFromEffect(String effect) {
		GoapState state = null;

		for (GoapState goalState : this.playerUnit.getGoalState()) {
			if (goalState.effect.equals(effect)) {
				state = goalState;
			}
		}
		return state;
	}
}
