package unitControlModule.goapActionTaking;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;

/**
 * IdleState.java --- State on the FSM Stack
 * 
 * @author P H - 28.01.2017
 */
class IdleState implements IFSMState {

	private List<GoapState> previousGoalState;
	private HashSet<GoapState> previousWorldState;
	private HashSet<GoapAction> previousAvailableActions;

	private List<Object> planCreatedListeners = new ArrayList<Object>();

	public IdleState() {

	}

	// -------------------- Functions

	@Override
	public boolean runGoapAction(GoapUnit goapUnit) {
		boolean defaultValuesSet = false;

		// Default values in the beginning
		if (this.previousGoalState == null && this.previousWorldState == null
				&& this.previousAvailableActions == null) {
			this.copyLists(goapUnit);

			defaultValuesSet = true;
		}

		// TODO: Possible Change: Change this system since it would cause a lot
		// of time wasted

		// Any state changed causes the idle state to try to plan a new Queue of
		// GoapActions
		// if (defaultValuesSet ||
		// !this.previousGoalState.equals(goapUnit.getGoalState())
		// || !this.previousWorldState.equals(goapUnit.getWorldState())
		// ||
		// !this.previousAvailableActions.equals(goapUnit.getAvailableActions()))
		// {
		Queue<GoapAction> plannedQueue = GoapPlanner.plan(goapUnit);

		if (plannedQueue != null) {
			this.dispatchNewPlanCreatedEvent(plannedQueue);
		} else {
			// No plan was created = no combination of the states needs to
			// be planned for the current goals
			this.copyLists(goapUnit);
		}
		// }

		// Returning false would result in the RunActionState, which gets added
		// to the Stack by the Agent, to be removed.
		return true;
	}

	/**
	 * Function for preserving the states of a goapUnit in sublists of this
	 * instance.
	 *
	 * @param goapUnit
	 *            the unit whose states are being saved in sublists.
	 */
	private void copyLists(GoapUnit goapUnit) {
		this.previousGoalState = new ArrayList<GoapState>(goapUnit.getGoalState());
		this.previousWorldState = new HashSet<GoapState>(goapUnit.getWorldState());
		this.previousAvailableActions = new HashSet<GoapAction>(goapUnit.getAvailableActions());
	}

	// -------------------- Events

	// ------------------------------ Plan created
	synchronized void addPlanCreatedListener(Object listener) {
		this.planCreatedListeners.add(listener);
	}

	synchronized void removePlanCreatedListener(Object listener) {
		this.planCreatedListeners.remove(listener);
	}

	private synchronized void dispatchNewPlanCreatedEvent(Queue<GoapAction> plan) {
		for (Object listener : this.planCreatedListeners) {
			((PlanCreatedEventListener) listener).onPlanCreated(plan);
		}
	}
}
