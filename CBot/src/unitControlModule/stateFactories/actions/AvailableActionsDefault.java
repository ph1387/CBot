package unitControlModule.stateFactories.actions;

import javaGOAP.GoapAction;
import unitControlModule.stateFactories.actions.executableActions.AttackMoveAction;
import unitControlModule.stateFactories.actions.executableActions.AttackUnitAction;
import unitControlModule.stateFactories.actions.executableActions.ScoutBaseLocationAction;

/**
 * SimpleUnitAvailableActions.java --- A simple HashSet for a Unit containing
 * all basic Actions.
 * 
 * @author P H - 26.02.2017
 *
 */
public class AvailableActionsDefault extends AvailableActionsGeneral {

	public AvailableActionsDefault() {
		this.add(this.defineScoutingAction());
		this.add(this.defineAttackMoveAction());
		this.add(new AttackUnitAction(null));
	}

	/**
	 * Function for defining a scouting action for the Unit. These might differ
	 * from one another. Therefore a function is needed.
	 * 
	 * @return a {@link GoapAction} which defines a scouting action.
	 */
	protected GoapAction defineScoutingAction() {
		return new ScoutBaseLocationAction(null);
	}

	// TODO: UML ADD
	/**
	 * Function for defining a attack move action for the Unit. These might
	 * differ from one another. Therefore a function is needed.
	 * 
	 * @return a {@link GoapAction} which defines a attack move action.
	 */
	protected GoapAction defineAttackMoveAction() {
		return new AttackMoveAction(null);
	}
}
