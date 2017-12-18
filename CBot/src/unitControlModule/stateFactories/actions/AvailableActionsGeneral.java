package unitControlModule.stateFactories.actions;

import java.util.HashSet;

import javaGOAP.GoapAction;
import unitControlModule.stateFactories.actions.executableActions.GroupingAtPositionActionBaseEntrance;
import unitControlModule.stateFactories.actions.executableActions.RetreatActionInPreviousAdjacentRegion;
import unitControlModule.stateFactories.actions.executableActions.RetreatActionSteerInRetreatVectorDirection;

/**
 * AvailableActionsGeneral.java --- Top most HashSet containing Actions which
 * are shared by all Units. Also provides some basic functions for removing
 * already existing Actions.
 * 
 * @author P H - 27.06.2017
 *
 */
public abstract class AvailableActionsGeneral extends HashSet<GoapAction> {

	public AvailableActionsGeneral() {
		this.add(new RetreatActionSteerInRetreatVectorDirection(null));
		this.add(new RetreatActionInPreviousAdjacentRegion(null));
		this.add(new GroupingAtPositionActionBaseEntrance(null));
	}

	// -------------------- Functions

	/**
	 * Function for removing a GoapAction matching a given Class from the
	 * collection of stored Actions.
	 * 
	 * @param instanceClass
	 *            the Type the Actions must be an instance of.
	 * @return true or false depending if a Action was removed or not.
	 */
	protected <T> boolean removeAction(Class<T> instanceClass) {
		GoapAction actionMatch = null;
		boolean success = false;

		for (GoapAction action : this) {
			if (instanceClass.isInstance(action)) {
				actionMatch = action;

				break;
			}
		}

		// Remove the Action matching the Class if one is found.
		if (actionMatch != null) {
			this.remove(actionMatch);

			success = true;
		}
		return success;
	}
}
