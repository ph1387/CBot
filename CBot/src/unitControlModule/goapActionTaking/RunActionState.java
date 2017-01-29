package unitControlModule.goapActionTaking;

import java.util.Queue;

final class RunActionState implements IFSMState {
	/**
	 * RunActionState.java --- State on the FSM Stack
	 * 
	 * @author P H - 28.01.2017
	 */

	private Queue<GoapAction> currentActions;
	private FSM fsm;

	RunActionState(FSM fsm, Queue<GoapAction> currentActions) {
		this.fsm = fsm;
		this.currentActions = currentActions;
	}

	// -------------------- Functions

	/**
	 * Cycle trough all actions until an invalid one or the end of the Queue is
	 * reached. A false return type here causes the FSM to pop the state from
	 * its stack.
	 * 
	 * @see unitControlModule.goapActionTaking.IFSMState#runGoapAction(unitControlModule.goapActionTaking.GoapUnit)
	 */
	@Override
	public boolean runGoapAction(GoapUnit goapUnit) throws Exception {
		boolean workingOnQueue = false;

		if (this.currentActions.peek().isDone()) {
			this.currentActions.poll();
		}

		if (!this.currentActions.isEmpty()) {
			GoapAction currentAction = this.currentActions.peek();

			if (currentAction.target == null) {
				throw new Exception("Target is null!");
			} else if (currentAction.requiresInRange(goapUnit) && !currentAction.isInRange(goapUnit)) {
				this.fsm.pushStack(new MoveToState(currentAction.target, currentAction));
			} else if (currentAction.checkProceduralPrecondition(goapUnit)
					&& !currentAction.performAction(goapUnit)) {
				throw new Exception("Action not possible (ProceduralPrecondition)!");
			}

			workingOnQueue = true;
		}
		return workingOnQueue;
	}
	
	// ------------------------------ Getter / Setter
	
	Queue<GoapAction> getCurrentActions() {
		return this.currentActions;
	}
}
