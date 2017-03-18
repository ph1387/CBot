package unitControlModule.stateFactories.updater;

import javaGOAP.GoapAction;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * GeneralActionUpdater.java --- Superclass for updating most AvailableActions.
 * 
 * @author P H - 26.02.2017
 *
 */
public abstract class GeneralActionUpdater implements Updater {

	protected PlayerUnit playerUnit;

	public GeneralActionUpdater(PlayerUnit playerUnit) {
		this.playerUnit = playerUnit;
	}

	// -------------------- Functions

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
