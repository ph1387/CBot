package buildingOrderModule.stateFactories.actions.executableActions;

import javaGOAP.IGoapUnit;

/**
 * BuildWorkerAction.java --- Action for building a race specific worker Unit.
 * 
 * @author P H - 28.04.2017
 *
 */
public class BuildWorkerAction extends ManagerBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public BuildWorkerAction(Object target) {
		super(target);

		// this.addEffect(new GoapState(0, "unitsNeeded", false));
		// this.addPrecondition(new GoapState(0, "unitsNeeded", true));
	}

	// -------------------- Functions

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		return true;
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return 0;
	}

	@Override
	protected boolean isDone(IGoapUnit goapUnit) {
		return false;
	}

	@Override
	protected boolean performAction(IGoapUnit goapUnit) {
		return true;
	}

	// ------------------------------ Getter / Setter

}
