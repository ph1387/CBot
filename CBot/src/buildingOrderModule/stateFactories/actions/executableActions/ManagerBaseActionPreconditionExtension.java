package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import bwapi.Unit;
import bwapi.UnitType;
import core.Core;
import javaGOAP.IGoapUnit;

/**
 * ManagerBaseActionPreconditionExtension.java --- Extension for the
 * ManagerBaseAction due to Upgrades, Research and Addons not being limited by
 * the limited concurrent amount of queued elements in the information
 * preserver.
 * 
 * @author P H - 30.04.2017
 *
 */
public abstract class ManagerBaseActionPreconditionExtension extends ManagerBaseAction {

	/**
	 * Interface for checking the preconditions of the different subclasses.
	 */
	protected interface PreconditionChecker {
		boolean check(Unit unit);

		boolean check(UnitType unitType);
	}

	private PreconditionChecker checker;

	/**
	 * @param target
	 *            type: Integer
	 */
	public ManagerBaseActionPreconditionExtension(Object target) {
		super(target);

		this.checker = this.definePreconditionChecker();
	}

	// -------------------- Functions

	/**
	 * Function needed for checking if certain criteria are matched.
	 * 
	 * @return a PreconditionChecker object which can be used for checking the
	 *         procedural preconditions.
	 */
	protected abstract PreconditionChecker definePreconditionChecker();

	/**
	 * Function is not used in this context, since
	 * {@link #checkProceduralPrecondition(IGoapUnit)} overrides the
	 * superclass's function!
	 */
	@Override
	protected boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit) {
		// Does not matter since this function is never called in this context!
		return true;
	}

	@Override
	public boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		boolean success = false;

		// First check all current Units.
		for (Unit unit : Core.getInstance().getPlayer().getUnits()) {
			if (this.checker.check(unit)) {
				success = true;
				break;
			}
		}

		// Then check success if any building is queued that can use the
		// subclass's feature.
		if (!success) {
			for (UnitType unitType : ((BuildActionManager) goapUnit).getInformationStorage().getWorkerConfig()
					.getBuildingQueue()) {
				if (this.checker.check(unitType)) {
					success = true;
					break;
				}
			}
		}

		// As well as all buildings currently being build.
		if (!success) {
			for (Unit unit : ((BuildActionManager) goapUnit).getInformationStorage().getWorkerConfig()
					.getBuildingsBeingCreated()) {
				if (this.checker.check(unit)) {
					success = true;
					break;
				}
			}
		}

		return success;
	}
}
