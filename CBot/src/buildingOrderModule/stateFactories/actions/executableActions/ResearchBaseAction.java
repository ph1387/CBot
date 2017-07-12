package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.simulator.ActionType;
import buildingOrderModule.simulator.TypeWrapper;
import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;
import core.Core;
import javaGOAP.IGoapUnit;

// TODO: UML ADD
/**
 * ResearchBaseAction.java --- Superclass for all research related actions.
 * 
 * @author P H - 29.04.2017
 *
 */
public abstract class ResearchBaseAction extends ManagerBaseActionPreconditionExtension implements ActionType {

	/**
	 * Anonymous inner class for the precondition check.
	 */
	private class CustomPreconditionChecker implements PreconditionChecker {

		private ResearchBaseAction actionReference;

		public CustomPreconditionChecker(ResearchBaseAction typeReference) {
			this.actionReference = typeReference;
		}

		@Override
		public boolean check(Unit unit) {
			return unit.getType().isBuilding() && unit.canResearch(this.actionReference.type);
		}

		@Override
		public boolean check(UnitType unitType) {
			return unitType.researchesWhat().contains(this.actionReference.type)
					&& Core.getInstance().getPlayer().isResearchAvailable(this.actionReference.type);
		}
	}

	protected TechType type;

	/**
	 * @param target
	 *            type: Integer
	 */
	public ResearchBaseAction(Object target) {
		super(target);

		this.type = this.defineType();
	}

	// -------------------- Functions

	/**
	 * Function for defining the type of the technology being researched.
	 * 
	 * @return the TechType of the technology being researched.
	 */
	protected abstract TechType defineType();

	@Override
	protected void performSpecificAction(IGoapUnit goapUnit) {
		((BuildActionManager) goapUnit).getSender().researchTech(this.type);
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return this.type.researchTime() + this.type.mineralPrice() + this.type.gasPrice();
	}

	@Override
	protected PreconditionChecker definePreconditionChecker() {
		return new CustomPreconditionChecker(this);
	}
	


	
	
	
	
	// TODO: UML ADD FF
	@Override
	public int defineScore() {
		return this.defineType().mineralPrice() + this.defineType().gasPrice();
	}

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
		return this.defineType().researchTime();
	}

	@Override
	public TypeWrapper defineResultType() {
		return TypeWrapper.generateFrom(this.defineType());
	}
	
	@Override
	public TypeWrapper defineRequiredType() {
		return TypeWrapper.generateFrom(this.defineType().whatResearches());
	}
}
