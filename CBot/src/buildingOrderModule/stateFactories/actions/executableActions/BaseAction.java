package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import javaGOAP.GoapAction;
import javaGOAP.IGoapUnit;

// TODO: UML ADD
/**
 * BaseAction.java --- Superclass for the most basic tasks a building manager
 * may rely on as a GoapAction.
 * 
 * @author P H - 17.07.2017
 *
 */
public abstract class BaseAction extends GoapAction {

	/**
	 * @param target
	 *            type: Depends on the deriving Subclass.
	 */
	public BaseAction(Object target) {
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
