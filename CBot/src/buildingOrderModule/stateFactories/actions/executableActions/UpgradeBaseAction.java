package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.simulator.TypeWrapper;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;
import core.Core;
import javaGOAP.IGoapUnit;

/**
 * UpgradeBaseAction.java --- Superclass for all upgrade related actions.
 * 
 * @author P H - 29.04.2017
 *
 */
public abstract class UpgradeBaseAction extends ManagerBaseActionPreconditionExtension {

	/**
	 * Anonymous inner class for the precondition check.
	 */
	private class CustomPreconditionChecker implements PreconditionChecker {

		private UpgradeBaseAction actionReference;

		public CustomPreconditionChecker(UpgradeBaseAction typeReference) {
			this.actionReference = typeReference;
		}

		@Override
		public boolean check(Unit unit) {
			return unit.getType().isBuilding() && unit.canUpgrade(this.actionReference.type);
		}

		@Override
		public boolean check(UnitType unitType) {
			boolean maxUpgradeLevelNotReached = Core.getInstance().getPlayer()
					.getUpgradeLevel(this.actionReference.type) < Core.getInstance().getPlayer()
							.getMaxUpgradeLevel(this.actionReference.type);

			return unitType.upgradesWhat().contains(this.actionReference.type) && maxUpgradeLevelNotReached;
		}
	}

	protected UpgradeType type;

	/**
	 * @param target
	 *            type: Integer
	 */
	public UpgradeBaseAction(Object target) {
		super(target);

		this.type = this.defineType();
	}

	// -------------------- Functions

	/**
	 * Function for defining the type of the upgrade being constructed.
	 * 
	 * @return the UpgradeType of the upgrade being constructed.
	 */
	protected abstract UpgradeType defineType();

	@Override
	protected void performSpecificAction(IGoapUnit goapUnit) {
		((BuildActionManager) goapUnit).getSender().buildUpgrade(this.type);
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return this.type.upgradeTime() + this.type.mineralPrice() + this.type.gasPrice();
	}

	@Override
	protected PreconditionChecker definePreconditionChecker() {
		return new CustomPreconditionChecker(this);
	}

	// ------------------------------ ActionType

	@Override
	public int defineMineralCost() {
		return this.defineType().mineralPrice();
	}

	@Override
	public int defineGasCost() {
		return this.defineType().gasPrice();
	}

	@Override
	public int defineCompletionTime() {
		return this.defineType().upgradeTime();
	}

	@Override
	public TypeWrapper defineResultType() {
		return TypeWrapper.generateFrom(this.defineType());
	}

	@Override
	public TypeWrapper defineRequiredType() {
		return TypeWrapper.generateFrom(this.defineType().whatUpgrades());
	}

	@Override
	public int defineMaxSimulationOccurrences() {
		return 1;
	}

}
