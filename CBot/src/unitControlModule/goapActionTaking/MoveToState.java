package unitControlModule.goapActionTaking;

final class MoveToState implements IFSMState {
	/**
	 * MoveToState.java --- State on the FSM Stack
	 * 
	 * @author P H - 28.01.2017
	 */

	Object target;
	GoapAction currentAction;

	MoveToState(Object target, GoapAction currentAction) {
		this.target = target;
		this.currentAction = currentAction;
	}

	/**
	 * Move to the target of the currentAction until the unit is in range to
	 * perform the action itself.
	 * 
	 * @see unitControlModule.goapActionTaking.IFSMState#runGoapAction(unitControlModule.goapActionTaking.GoapUnit)
	 */
	@Override
	public boolean runGoapAction(GoapUnit goapUnit) {
		boolean movingFinished = false;

		if (!this.currentAction.requiresInRange(goapUnit) || this.currentAction.isInRange(goapUnit)
				|| this.currentAction.target == null) {
			movingFinished = true;
		} else {
			goapUnit.moveTo(this.target);
		}
		return movingFinished;
	}

}
