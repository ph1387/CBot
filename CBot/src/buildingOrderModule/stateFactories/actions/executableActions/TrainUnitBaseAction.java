package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.simulator.TypeWrapper;
import bwapi.Unit;
import bwapi.UnitType;
import core.CBot;
import core.Core;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;

/**
 * TrainUnitBaseAction.java --- Superclass for all Unit training actions.
 * 
 * @author P H - 29.04.2017
 *
 */
public abstract class TrainUnitBaseAction extends ManagerBaseAction {

	protected UnitType type;

	public TrainUnitBaseAction(Object target) {
		super(target);

		this.type = this.defineType();

		this.addEffect(new GoapState(0, "unitsNeeded", false));
		this.addPrecondition(new GoapState(0, "unitsNeeded", true));
	}

	// -------------------- Functions

	/**
	 * Function for defining the type of Unit that the action will train.
	 * 
	 * @return the UnitType of the Unit that the action will train.
	 */
	protected abstract UnitType defineType();

	@Override
	public boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		// Only train units if the building queue is empty
		return CBot.getInstance().getWorkerManagerConstructionJobDistribution().getBuildingQueue().isEmpty()
				&& this.checkProceduralSpecificPrecondition(goapUnit);
	}

	@Override
	protected boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit) {
		boolean success = false;

		for (Unit unit : Core.getInstance().getPlayer().getUnits()) {
			if (unit.getType().isBuilding() && unit.canTrain(this.type)) {
				success = true;
				break;
			}
		}
		return success;
	}

	@Override
	protected void performSpecificAction(IGoapUnit goapUnit) {
		((BuildActionManager) goapUnit).getSender().buildUnit(this.type);
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		int facilities = this.getPossibleTrainingFacilitiesCount();

		if (facilities == 0) {
			facilities = 1;
		}

		return (((int) this.target - this.iterationCount) * this.defineBaseCost()) / facilities;
	}

	/**
	 * Function for defining the base cost of the action.
	 * 
	 * @return the cost of the the action.
	 */
	protected int defineBaseCost() {
		return this.type.buildTime() + this.type.mineralPrice() + this.type.gasPrice();
	}

	/**
	 * Function for counting the amount of training facilities that are able to
	 * train the specified UnitType.
	 * 
	 * @return the amount of training facilities that are able to train the
	 *         UnitType.
	 */
	protected int getPossibleTrainingFacilitiesCount() {
		int count = 0;

		for (Unit unit : Core.getInstance().getPlayer().getUnits()) {
			if (unit.getType().isBuilding() && unit.canTrain(this.type)) {
				count++;
			}
		}

		return count;
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
		return this.defineType().buildTime();
	}

	@Override
	public TypeWrapper defineResultType() {
		return TypeWrapper.generateFrom(this.defineType());
	}

	@Override
	public int defineMaxSimulationOccurrences() {
		int maxPossTrainingCount = this.defineMaxTrainingCount();
		int queuedTrainingCount = 0;

		// Count the number of times the UnitType is already queued.
		for (UnitType unitType : CBot.getInstance().getInformationStorage().getTrainingQueue()) {
			if (unitType == this.defineType()) {
				queuedTrainingCount++;
			}
		}

		return maxPossTrainingCount - queuedTrainingCount;
	}

	/**
	 * Function for defining the maximum number of times the UnitType may be
	 * queued for training. This function is necessary since some Units
	 * (Terran_Science_Vessel, Terran_Siege_Tank, etc.) require additional
	 * addons connected to their training facility, therefore limiting the
	 * maximum number of simultaneously queued elements not to the number of
	 * training facilities, but to the number of addons!
	 * 
	 * @return the maximum number of times the UnitType may be used in a single
	 *         simulation cycle.
	 */
	protected int defineMaxTrainingCount() {
		return CBot.getInstance().getInformationStorage().getCurrentGameInformation().getCurrentUnitCounts()
				.getOrDefault(this.defineRequiredType().getUnitType(), 0);
	}

}
