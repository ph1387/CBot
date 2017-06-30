package unitControlModule.stateFactories.updater;

import javaGOAP.GoapAction;
import unitControlModule.stateFactories.actions.executableActions.RetreatActionSteerInGoalDirection;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * GeneralActionUpdater.java --- Superclass for updating most AvailableActions.
 * 
 * @author P H - 26.02.2017
 *
 */
public abstract class ActionUpdaterGeneral implements Updater {

	protected PlayerUnit playerUnit;

	public ActionUpdaterGeneral(PlayerUnit playerUnit) {
		this.playerUnit = playerUnit;
	}

	// -------------------- Functions

	@Override
	public void update(PlayerUnit playerUnit) {
		// Update the retreating action of the Unit. This is the default
		// retreating Action a Unit can perform. If another one is desired, this
		// function needs to be overwritten.
		if (this.playerUnit.currentState == PlayerUnit.UnitStates.ENEMY_KNOWN) {
			((RetreatActionSteerInGoalDirection) this.getActionFromInstance(RetreatActionSteerInGoalDirection.class))
					.setTarget(this.playerUnit.getClosestEnemyUnitInConfidenceRange());
		}
	}

	/**
	 * Get the GoapAction from the availableActions HashSet that is an instance
	 * of the specific class.
	 * 
	 * @param instanceClass
	 *            the class of which an instance is being searched in the
	 *            availableActions HashSet.
	 * @return the action that is an instance of the given class.
	 */
	protected <T> GoapAction getActionFromInstance(Class<T> instanceClass) {
		GoapAction actionMatch = null;

		for (GoapAction action : this.playerUnit.getAvailableActions()) {
			if (instanceClass.isInstance(action)) {
				actionMatch = action;

				break;
			}
		}
		return actionMatch;
	}
}
