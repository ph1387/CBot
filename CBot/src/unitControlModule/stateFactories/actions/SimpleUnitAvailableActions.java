package unitControlModule.stateFactories.actions;

import java.util.HashSet;

import unitControlModule.stateFactories.actions.executableActions.AttackMoveAction;
import unitControlModule.stateFactories.actions.executableActions.AttackUnitAction;
import unitControlModule.stateFactories.actions.executableActions.BaseAction;
import unitControlModule.stateFactories.actions.executableActions.RetreatFromNearestUnitAction;
import unitControlModule.stateFactories.actions.executableActions.ScoutBaseLocationAction;

/**
 * SimpleUnitAvailableActions.java --- A simple HashSet for a Unit containing
 * all basic Actions.
 * 
 * @author P H - 26.02.2017
 *
 */
public class SimpleUnitAvailableActions extends HashSet<BaseAction> {

	public SimpleUnitAvailableActions() {
		this.add(new ScoutBaseLocationAction(null));
		this.add(new AttackMoveAction(null));
		this.add(new AttackUnitAction(null));
		this.add(new RetreatFromNearestUnitAction(null));
	}
}
