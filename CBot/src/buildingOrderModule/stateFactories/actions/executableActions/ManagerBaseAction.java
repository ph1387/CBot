package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import javaGOAP.GoapAction;
import javaGOAP.IGoapUnit;

/**
 * ManagerBaseAction.java --- Superclass for BuildActionManager actions.
 * 
 * @author P H - 28.04.2017
 *
 */
public abstract class ManagerBaseAction extends GoapAction {

	protected int iterationCount = 0;

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
	public boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		boolean success = true;

		if (((BuildActionManager) goapUnit).getInformationStorage()
				.getTrainingAndBuildingQueueSize() > ((BuildActionManager) goapUnit).getInformationStorage()
						.getMaxConcurrentElements()) {
			success = false;
		}

		return success && this.checkProceduralSpecificPrecondition(goapUnit);
	}

	/**
	 * Function for checking a more specific precondition of any subclass.
	 * 
	 * @param goapUnit
	 *            the executing GoapUnit.
	 * @return true or false depending if the specific preconditions are met.
	 */
	protected abstract boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit);

	@Override
	public boolean performAction(IGoapUnit goapUnit) {
		if (this.checkProceduralPrecondition(goapUnit)) {
			this.performSpecificAction(goapUnit);
			this.iterationCount++;
		}
		return true;
	}

	protected abstract void performSpecificAction(IGoapUnit goapUnit);

	@Override
	public boolean isDone(IGoapUnit goapUnit) {
		return this.iterationCount >= (int) this.target;
	}

	@Override
	public void reset() {
		this.iterationCount = 0;
	}

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

	// ------------------------------ Getter / Setter

	public void setTarget(Object target) {
		this.target = target;
	}

	public Object getTarget() {
		return this.target;
	}
}
