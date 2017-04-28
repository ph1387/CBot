package buildingOrderModule.stateFactories.updater;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import javaGOAP.GoapAction;

//TODO: Needed Change: Combine with the UnitControl Updater
/**
 * ActionUpdaterGeneral.java --- Superclass for updating most AvailableActions.
 * 
 * @author P H - 28.04.2017
 *
 */
public abstract class ActionUpdaterGeneral implements Updater {

	protected BuildActionManager buildActionManager;

	public ActionUpdaterGeneral(BuildActionManager buildActionManager) {
		this.buildActionManager = buildActionManager;
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

		for (GoapAction action : this.buildActionManager.getAvailableActions()) {
			if (instanceClass.isInstance(action)) {
				actionMatch = action;

				break;
			}
		}
		return actionMatch;
	}
}
