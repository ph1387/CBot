package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import bwapi.Unit;
import bwapi.UnitType;
import javaGOAP.IGoapUnit;

/**
 * BuildAddonBaseAction.java --- Superclass for all addon specific actions.
 * 
 * @author P H - 29.04.2017
 *
 */
public abstract class BuildAddonBaseAction extends ManagerBaseActionPreconditionExtension {

	// Anonymous inner class for the precondition check.
	private class CustomPreconditionChecker implements PreconditionChecker {

		private BuildAddonBaseAction actionReference;

		public CustomPreconditionChecker(BuildAddonBaseAction typeReference) {
			this.actionReference = typeReference;
		}

		@Override
		public boolean check(Unit unit) {
			return unit.getType().isBuilding() && unit.canBuildAddon() && unit.canBuildAddon(this.actionReference.type);
		}

		@Override
		public boolean check(UnitType unitType) {
			// Is the UnitType that the action reference's type requires the
			// same as the given UnitType?
			return unitType.canBuildAddon() && this.actionReference.type.whatBuilds().first == unitType;
		}
	}

	protected UnitType type;

	public BuildAddonBaseAction(Object target) {
		super(target);

		this.type = this.defineType();
	}

	// -------------------- Functions

	/**
	 * Function for defining the type of the addon being constructed.
	 * 
	 * @return the UnitType of the addon being constructed.
	 */
	protected abstract UnitType defineType();

	@Override
	protected void performSpecificAction(IGoapUnit goapUnit) {
		((BuildActionManager) goapUnit).getSender().buildAddon(this.type);
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return this.type.buildTime() + this.type.mineralPrice() + this.type.gasPrice();
	}

	@Override
	protected PreconditionChecker definePreconditionChecker() {
		return new CustomPreconditionChecker(this);
	}
}
