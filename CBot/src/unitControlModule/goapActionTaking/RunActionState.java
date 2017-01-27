package unitControlModule.goapActionTaking;

import java.util.Queue;
//TODO: REMOVE
final class RunActionState implements IFSMState {

	private Queue<GoapAction> currentActions;
	
	RunActionState(Queue<GoapAction> currentActions) {
		this.currentActions = currentActions;
	}

	// -------------------- Functions

	// Cycle trough all actions until an invalid one or the end of the Queue is
	// reached. A false return type here causes the FSM to pop the state from
	// its stack.
	@Override
	public boolean runGoapAction(GoapUnit goapUnit) {
		boolean workingOnQueue = false;

		if (this.currentActions.peek().isDone()) {
			this.currentActions.poll();
		}

		if (!this.currentActions.isEmpty()) {
			try {
				GoapAction currentAction = this.currentActions.peek();

				if(currentAction.target == null) {
					throw new Exception("Target is null!");
				} else if (currentAction.checkProceduralPrecondition(goapUnit) && !currentAction.performAction(goapUnit)) {
					throw new Exception("Action failed!");
				}
				
				workingOnQueue = true;
			} catch (Exception e) {
				e.printStackTrace(); // TODO: Maybe add a System.out
			}
		}
		return workingOnQueue;
	}
}
