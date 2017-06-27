package unitControlModule.stateFactories.actions;

import java.util.HashSet;

import javaGOAP.GoapAction;
import unitControlModule.stateFactories.actions.executableActions.AttackMoveAction;
import unitControlModule.stateFactories.actions.executableActions.AttackUnitAction;
import unitControlModule.stateFactories.actions.executableActions.RetreatActionSteerInGoalDirection;
import unitControlModule.stateFactories.actions.executableActions.ScoutBaseLocationAction;

/**
 * SimpleUnitAvailableActions.java --- A simple HashSet for a Unit containing
 * all basic Actions.
 * 
 * @author P H - 26.02.2017
 *
 */
public class AvailableActionsDefault extends HashSet<GoapAction> {

	public AvailableActionsDefault() {
		this.add(this.defineScoutingAction());
		this.add(new AttackMoveAction(null));
		this.add(new AttackUnitAction(null));
		// this.add(new RetreatActionToFurthestUnitInCone(null));
		// this.add(new RetreatActionToOwnGatheringPoint(null));
		this.add(new RetreatActionSteerInGoalDirection(null));
	}

	/**
	 * Function for defining a scouting action for the Unit. These might differ
	 * from one another. Therefore a function is needed.
	 * 
	 * @return a GoapAction which defines a scouting action.
	 */
	protected GoapAction defineScoutingAction() {
		return new ScoutBaseLocationAction(null);
	}
}
