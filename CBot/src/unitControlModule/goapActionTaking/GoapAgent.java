package unitControlModule.goapActionTaking;

import java.util.Queue;

public class GoapAgent implements ImportantUnitGoalChangeEventListener, PlanCreatedEventListener {

	private FSM fsm = new FSM();
	private IdleState idleState = new IdleState();
	private GoapUnit assignedGoapUnit;

	public GoapAgent(GoapUnit assignedUnit) {
		this.assignedGoapUnit = assignedUnit;

		this.assignedGoapUnit.addImportantUnitGoalChangeListener(this);
		this.idleState.addPlanCreatedListener(this);
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
	// This event is needed to push real action Queues on the FSM-Stack. Has to
	// pop the FSM-Stack, since the event fires before the return value of the
	// state gets checked.
	@Override
	public void onPlanCreated(Queue<GoapAction> plan) {
		this.fsm.popStack();
		this.fsm.pushStack(new RunActionState(plan));
	}

	// ------------------------------ GoapUnit
	// This event is needed to change a current goal to a new one, while keeping
	// the old one on the FSM-Stack for its later Queue execution.
	@Override
	public void onImportantUnitGoalChange(GoapState newGoalState) {
		this.fsm.pushStack(this.idleState);
	}
}
