package unitControlModule.stateFactories.actions.executableActions;

import java.util.HashMap;

import javaGOAP.GoapAction;
import javaGOAP.IGoapUnit;
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
	private IGoapUnit currentlyExecutingUnit;

	public BaseAction(Object target) {
		super(target);
	}

	// -------------------- Functions

	@Override
	protected boolean performAction(IGoapUnit goapUnit) {
		BaseAction storedAction = BaseAction.currentlyExecutingActions.get((PlayerUnit) goapUnit);

		// Check if the executing GoapAction has changed and if it did, enable a
		// trigger on which the subclass can react to.
		if (storedAction != null && storedAction.equals(this)) {
			this.actionChangeTrigger = false;
		} else {
			this.actionChangeTrigger = true;
		}

		// Store the executed action in the HashMap as well as the executing
		// Unit separately for having access to the Action and reset it when it
		// finishes.
		BaseAction.currentlyExecutingActions.put((PlayerUnit) goapUnit, this);
		this.currentlyExecutingUnit = goapUnit;

		return this.performSpecificAction(goapUnit);
	}

	protected abstract boolean performSpecificAction(IGoapUnit goapUnit);

	/**
	 * Function used for resetting the GoapAction which was executed. This
	 * function gets called when the GoapAction finishes so that the
	 * actionTrigger is going to be enabled in the next iteration.
	 */
	protected void resetStoredAction() {
		BaseAction.currentlyExecutingActions.put((PlayerUnit) this.currentlyExecutingUnit, null);
	}

	// ------------------------------ Getter / Setter

	public void setTarget(Object target) {
		this.target = target;
	}
}
