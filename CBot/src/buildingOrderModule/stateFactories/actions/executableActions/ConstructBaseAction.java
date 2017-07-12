package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.simulator.ActionType;
import buildingOrderModule.simulator.TypeWrapper;
import bwapi.TechType;
import bwapi.UnitType;
import core.Core;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;

// TODO: UML ADD
/**
 * ConstructBaseAction.java --- Superclass for all construction actions.
 * 
 * @author P H - 30.04.2017
 *
 */
public abstract class ConstructBaseAction extends ManagerBaseAction implements ActionType {

	protected UnitType type;

	/**
	 * @param target
	 *            type: Integer
	 */
	public ConstructBaseAction(Object target) {
		super(target);

		this.type = this.defineType();

		this.addEffect(new GoapState(0, "buildingsNeeded", false));
		this.addPrecondition(new GoapState(0, "buildingsNeeded", true));
	}

	// -------------------- Functions

	/**
	 * Function for defining the type of building that the action will be
	 * constructing.
	 * 
	 * @return the UnitType of the building that the action will construct.
	 */
	protected abstract UnitType defineType();

	@Override
	protected boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit) {
		boolean success = true;

		if (this.type.requiredTech() != TechType.None) {
			success = Core.getInstance().getPlayer().hasResearched(this.type.requiredTech());
		}
		return success;
	}

	@Override
	protected void performSpecificAction(IGoapUnit goapUnit) {
		((BuildActionManager) goapUnit).getSender().buildBuilding(this.type);
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return ((int) this.target - this.iterationCount) * this.defineBaseCost();
	}

	/**
	 * Function for defining the base cost of the action.
	 * 
	 * @return the cost of the the action.
	 */
	protected int defineBaseCost() {
		return this.type.buildTime() + this.type.mineralPrice() + this.type.gasPrice();
	}
	

	
	
	
	
	// TODO: UML ADD FF
	@Override
	public int defineScore() {
		return this.defineResultType().getUnitType().mineralPrice() + this.defineResultType().getUnitType().gasPrice();
	}

	@Override
	public int defineMineralCost() {
		return this.defineResultType().getUnitType().mineralPrice();
	}

	@Override
	public int defineGasCost() {
		return this.defineResultType().getUnitType().gasPrice();
	}

	@Override
	public int defineCompletionTime() {
		return this.defineResultType().getUnitType().buildTime();
	}

	@Override
	public TypeWrapper defineResultType() {
		return TypeWrapper.generateFrom(this.defineType());
	}
	
	@Override
	public TypeWrapper defineRequiredType() {
		return TypeWrapper.generateFrom(Core.getInstance().getPlayer().getRace().getWorker());
	}
	
}
