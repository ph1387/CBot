package unitControlModule.goapActionTaking;

import java.util.Queue;

interface FSMPlanEventListener {
	/**
	 * Gets called when a RunActionState on the FSM throws an exception.
	 *
	 * @param actions the action Queue which failed to execute.
	 */
	public void onPlanFailed(Queue<GoapAction> actions);
	
	/**
	 * Gets called when a RunActionState on the FSM returns true and therefore signals that it is finished.
	 *
	 * @param actions the action Queue which finished.
	 */
	public void onPlanFinished(Queue<GoapAction> actions);
}
