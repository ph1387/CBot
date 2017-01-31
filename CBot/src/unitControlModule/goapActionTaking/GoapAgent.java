package unitControlModule.goapActionTaking;

import java.util.Queue;

/**
 * GoapAgent.java --- The Agent which controls a units actions
 * 
 * @author P H - 28.01.2017
 */
public class GoapAgent implements ImportantUnitGoalChangeEventListener, PlanCreatedEventListener, FSMPlanEventListener {

	private FSM fsm = new FSM();
	private IdleState idleState = new IdleState();
	private GoapUnit assignedGoapUnit;

	/**
	 * @param assignedUnit the GoapUnit the agent works with.
	 */
	public GoapAgent(GoapUnit assignedUnit) {
		this.assignedGoapUnit = assignedUnit;

		this.assignedGoapUnit.addImportantUnitGoalChangeListener(this);
		this.idleState.addPlanCreatedListener(this);
		this.fsm.addPlanEventListener(this);
	}

	// -------------------- Functions

	public void update() {
		if (!this.fsm.hasStates()) {
			this.fsm.pushStack(this.idleState);
		}

		this.assignedGoapUnit.update();
		this.fsm.update(this.assignedGoapUnit);
	}

	// ------------------------------ Getter / Setter

	public GoapUnit getAssignedGoapUnit() {
		return this.assignedGoapUnit;
	}

	// -------------------- Eventlisteners

	// ------------------------------ IdleState
	/**
	 * This event is needed to push real action Queues on the FSM-Stack. Has to
	 * pop the FSM-Stack, since the event fires before the return value of the
	 * state gets checked.
	 * 
	 * @see unitControlModule.goapActionTaking.PlanCreatedEventListener#onPlanCreated(java.util.Queue)
	 */
	@Override
	public void onPlanCreated(Queue<GoapAction> plan) {
		this.fsm.popStack();
		this.fsm.pushStack(new RunActionState(this.fsm, plan));
	}

	// ------------------------------ GoapUnit
	/**
	 * This event is needed to change a current goal to a new one, while keeping
	 * the old one on the FSM-Stack for its later Queue execution.
	 * 
	 * @see unitControlModule.goapActionTaking.ImportantUnitGoalChangeEventListener#onImportantUnitGoalChange(unitControlModule.goapActionTaking.GoapState)
	 */
	@Override
	public void onImportantUnitGoalChange(GoapState newGoalState) {
		this.fsm.pushStack(this.idleState);
	}

	// ------------------------------ FSM
	@Override
	public void onPlanFailed(Queue<GoapAction> actions) {
		this.assignedGoapUnit.goapPlanFailed(actions);
	}

	@Override
	public void onPlanFinished(Queue<GoapAction> actions) {
		this.assignedGoapUnit.goapPlanFinished();
	}
}
