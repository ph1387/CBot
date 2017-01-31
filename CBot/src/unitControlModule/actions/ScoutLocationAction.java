package unitControlModule.actions;

import unitControlModule.goapActionTaking.GoapAction;
import unitControlModule.goapActionTaking.GoapUnit;

/**
 * ScoutLocationAction.java --- Superclass for scouting actions
 * @author P H - 30.01.2017
 *
 */
public abstract class ScoutLocationAction extends GoapAction {
	
	protected static Integer RANGE_TO_TARGET = null;
	
	public ScoutLocationAction(Object target) {
		super(target);
	}

	// -------------------- Functions
	
	@Override
	protected void reset() {

	}

	@Override
	protected boolean isDone(GoapUnit goapUnit) {
		return this.isInRange(goapUnit);
	}

	@Override
	protected boolean performAction(GoapUnit goapUnit) {
		return true;
	}

	@Override
	protected float generateBaseCost(GoapUnit goapUnit) {
		return 0;
	}

	@Override
	protected boolean checkProceduralPrecondition(GoapUnit goapUnit) {
		return this.target != null;
	}

	@Override
	protected boolean requiresInRange(GoapUnit goapUnit) {
		return true;
	}

	// ------------------------------ Getter / Setter

	public void setTarget(Object target) {
		this.target = target;
	}

}
