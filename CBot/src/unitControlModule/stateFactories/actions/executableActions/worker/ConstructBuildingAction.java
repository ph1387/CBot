package unitControlModule.stateFactories.actions.executableActions.worker;

import core.CBot;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnitWorker;
import workerManagerConstructionJobDistribution.IConstrucionInformation;
import workerManagerConstructionJobDistribution.WorkerManagerConstructionJobDistribution;

/**
 * ConstructBuildingAction.java --- Constructing action for a PlayerUnitWorker
 * to construct a building.
 * 
 * @author P H - 02.04.2017
 *
 */
public class ConstructBuildingAction extends WorkerAction {

	private WorkerManagerConstructionJobDistribution workerManagerConstructionJobDistribution = CBot.getInstance()
			.getWorkerManagerConstructionJobDistribution();

	/**
	 * @param target
	 *            type: Null
	 */
	public ConstructBuildingAction(Object target) {
		super(new Object());

		this.addEffect(new GoapState(0, "constructing", true));
		this.addPrecondition(new GoapState(0, "canMove", true));
		this.addPrecondition(new GoapState(0, "isCarryingMinerals", false));
		this.addPrecondition(new GoapState(0, "isCarryingGas", false));
	}

	// -------------------- Functions

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		boolean success = false;

		// Assign the worker to a ConstructionJob if necessary.
		if (!this.workerManagerConstructionJobDistribution.isAssignedConstructing((PlayerUnitWorker) goapUnit)) {
			success = this.workerManagerConstructionJobDistribution.addWorker((PlayerUnitWorker) goapUnit);
		}

		// Checked again due to the possibility of the worker not being assigned
		// to a ConstructionJob.
		if (this.workerManagerConstructionJobDistribution.isAssignedConstructing((PlayerUnitWorker) goapUnit)) {
			IConstrucionInformation constructionInformation = this.workerManagerConstructionJobDistribution
					.getConstructionInformation((PlayerUnitWorker) goapUnit);

			// Update the ConstructionJob first.
			constructionInformation.update();

			// Initiate the construction of the building if necessary.
			if (!constructionInformation.constructionStarted()) {
				((PlayerUnitWorker) goapUnit).getUnit().build(constructionInformation.getUnitType(),
						constructionInformation.getTilePosition());
			}

			success = true;
		}

		return success;
	}

	@Override
	protected void resetSpecific() {
		// If the worker was assigned a ConstructionJob, remove the association.
		if (this.workerManagerConstructionJobDistribution.isAssignedConstructing(
				(PlayerUnitWorker) this.currentlyExecutingUnit) && this.currentlyExecutingUnit != null) {
			this.workerManagerConstructionJobDistribution.removeWorker((PlayerUnitWorker) this.currentlyExecutingUnit);
		}

		this.target = new Object();
	}

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		return this.workerManagerConstructionJobDistribution.isAssignedConstructing((PlayerUnitWorker) goapUnit)
				|| this.workerManagerConstructionJobDistribution.canConstruct();
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		// 1 since it should not be randomly added to other action Queues.
		return 1;
	}

	@Override
	protected float generateCostRelativeToTarget(IGoapUnit goapUnit) {
		return 0;
	}

	@Override
	protected boolean isDone(IGoapUnit goapUnit) {
		return this.workerManagerConstructionJobDistribution.isFinishedConstructing((PlayerUnitWorker) goapUnit);
	}

	// ------------------------------ Getter / Setter

}
