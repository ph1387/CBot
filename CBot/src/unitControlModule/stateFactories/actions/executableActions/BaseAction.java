package unitControlModule.stateFactories.actions.executableActions;

import java.util.HashMap;

import unitControlModule.goapActionTaking.GoapAction;
import unitControlModule.goapActionTaking.GoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

// TODO: UML
/**
 * BaseAction.java --- Superclass for all PlayerUnit actions.
 * 
 * @author P H - 09.02.2017
 *
 */
public abstract class BaseAction extends GoapAction {

	protected static HashMap<PlayerUnit, BaseAction> currentlyExecutingActions = new HashMap<>();

	protected boolean actionChangeTrigger = false;

	public BaseAction(Object target) {
		super(target);
	}

	// -------------------- Functions

	@Override
	protected boolean performAction(GoapUnit goapUnit) {
		BaseAction storedAction = BaseAction.currentlyExecutingActions.get((PlayerUnit) goapUnit);
		
		// Check if the executing GoapAction has changed and if it did, enable a
		// trigger on which the subclass can react to.
		if (storedAction != null && storedAction.equals(this)) {
			this.actionChangeTrigger = false;
		} else {
			this.actionChangeTrigger = true;
		}

		BaseAction.currentlyExecutingActions.put((PlayerUnit) goapUnit, this);

		return this.performSpecificAction(goapUnit);
	}

	protected abstract boolean performSpecificAction(GoapUnit goapUnit);

	// ------------------------------ Getter / Setter

	public void setTarget(Object target) {
		this.target = target;
	}
}
