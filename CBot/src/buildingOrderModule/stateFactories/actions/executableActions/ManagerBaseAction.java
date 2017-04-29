package buildingOrderModule.stateFactories.actions.executableActions;

import javaGOAP.GoapAction;
import javaGOAP.IGoapUnit;

/**
 * ManagerBaseAction.java --- Superclass for BuildActionManager actions.
 * 
 * @author P H - 28.04.2017
 *
 */
public abstract class ManagerBaseAction extends GoapAction {

	/**
	 * @param target
	 *            type: Integer, the amount of times the Unit, Upgrade etc. must
	 *            be build.
	 */
	public ManagerBaseAction(Object target) {
		super(target);
	}

	// -------------------- Functions

	@Override
	protected float generateCostRelativeToTarget(IGoapUnit goapUnit) {
		return 0;
	}

	@Override
	protected boolean isInRange(IGoapUnit goapUnit) {
		return false;
	}

	@Override
	protected boolean requiresInRange(IGoapUnit goapUnit) {
		return false;
	}

	@Override
	protected void reset() {
		this.target = new Integer(0);
	}

	// ------------------------------ Getter / Setter

	public void setTarget(Object target) {
		this.target = target;
	}

	public Object getTarget() {
		return this.target;
	}
}
