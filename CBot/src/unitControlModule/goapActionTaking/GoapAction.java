package unitControlModule.goapActionTaking;

import java.util.HashSet;

public abstract class GoapAction {

	protected Object target;

	private HashSet<GoapState> preconditions = new HashSet<GoapState>();
	private HashSet<GoapState> effects = new HashSet<GoapState>();

	public GoapAction(Object target) {
		this.target = target;
	}

	// -------------------- Functions

	// Set the target after a reset or the FSM will dispose of the Queue of this
	// GoapAction!
	void doReset() {
		reset();

		this.target = null;
		this.preconditions = new HashSet<GoapState>();
		this.effects = new HashSet<GoapState>();
	}

	protected abstract void reset();

	protected abstract boolean isDone();

	protected abstract boolean performAction(GoapUnit goapUnit);

	// This function will be called for each GoapAction in the generation of
	// each Graph to determine the cost for i.e. the movement cost to another
	// position!
	protected abstract float generateCost(GoapUnit goapUnit);

	protected abstract boolean checkProceduralPrecondition(GoapUnit goapUnit);

	// ------------------------------ Getter / Setter
	
	protected HashSet<GoapState> getPreconditions() {
		return this.preconditions;
	}
	
	protected HashSet<GoapState> getEffects() {
		return this.effects;
	}
	
	// ------------------------------ Others

	// ------------------------------ Preconditions
	// Overloaded function for convenience
	protected void addPrecondition(int importance, String effect, Object value) {
		this.addPrecondition(new GoapState(importance, effect, value));
	}

	// Add a precondition, which is not already in the HashSet
	protected void addPrecondition(GoapState precondition) {
		boolean alreadyInList = false;

		for (GoapState goapState : this.preconditions) {
			if (goapState.equals(precondition)) {
				alreadyInList = true;
			}
		}

		if (!alreadyInList) {
			this.preconditions.add(precondition);
		}
	}

	// Overloaded function for convenience
	protected boolean removePrecondition(GoapState precondition) {
		return this.removePrecondition(precondition.effect);
	}

	// Remove a precondition from the HashSet
	protected boolean removePrecondition(String preconditionEffect) {
		GoapState stateToBeRemoved = null;

		for (GoapState goapState : this.effects) {
			if (goapState.effect.equals(preconditionEffect)) {
				stateToBeRemoved = goapState;
			}
		}

		if (stateToBeRemoved != null) {
			this.preconditions.remove(stateToBeRemoved);
			return true;
		} else {
			return false;
		}
	}

	// ------------------------------ Effects
	// Overloaded function for convenience
	protected void addEffect(int importance, String effect, Object value) {
		this.addEffect(new GoapState(importance, effect, value));
	}

	// Add a effect, which is not already in the HashSet
	protected void addEffect(GoapState effect) {
		boolean alreadyInList = false;

		for (GoapState goapState : this.effects) {
			if (goapState.equals(effect)) {
				alreadyInList = true;
			}
		}

		if (!alreadyInList) {
			this.effects.add(effect);
		}
	}

	// Overloaded function for convenience
	protected boolean removeEffect(GoapState effect) {
		return this.removeEffect(effect.effect);
	}

	// Remove a effect from the HashSet
	protected boolean removeEffect(String effectEffect) {
		GoapState stateToBeRemoved = null;

		for (GoapState goapState : this.effects) {
			if (goapState.effect.equals(effectEffect)) {
				stateToBeRemoved = goapState;
			}
		}

		if (stateToBeRemoved != null) {
			this.effects.remove(stateToBeRemoved);
			return true;
		} else {
			return false;
		}
	}
}
