package buildingOrderModule.stateFactories.actions;

import java.util.HashSet;

import buildingOrderModule.stateFactories.actions.executableActions.BuildWorkerAction;
import javaGOAP.GoapAction;

/**
 * AvailableActionsDefault.java --- Default available actions for a
 * BuildActionManager.
 * 
 * @author P H - 28.04.2017
 *
 */
public class AvailableActionsDefault extends HashSet<GoapAction> {

	public AvailableActionsDefault() {
		this.add(new BuildWorkerAction(0));
	}
}
