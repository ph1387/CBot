package unitControlModule.actions;

import unitControlModule.goapActionTaking.GoapAction;

/**
 * BaseAction.java --- Superclass for most actions.
 * @author P H - 09.02.2017
 *
 */
public abstract class BaseAction extends GoapAction {

	public BaseAction(Object target) {
		super(target);
	}

	// -------------------- Functions
	
	@Override
	protected void reset() {
		
	}

	// ------------------------------ Getter / Setter

	public void setTarget(Object target) {
		this.target = target;
	}
}
