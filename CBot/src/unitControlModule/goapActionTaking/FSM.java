package unitControlModule.goapActionTaking;

import java.util.Stack;

final class FSM {

	private Stack<IFSMState> states = new Stack<IFSMState>();

	public FSM() {

	}

	// -------------------- Functions

	// Run through all action in the specific states
	void update(GoapUnit goapUnit) {
		if (!this.states.isEmpty() && !this.states.peek().runGoapAction(goapUnit)) {
			this.states.pop();
		}
	}

	void pushStack(IFSMState state) {
		this.states.push(state);
	}

	void popStack() {
		this.states.pop();
	}

	void clearStack() {
		this.states.clear();
	}

	boolean hasStates() {
		return !(this.states.isEmpty());
	}
}
