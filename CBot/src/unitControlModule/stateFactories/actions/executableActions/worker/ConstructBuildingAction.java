package unitControlModule.stateFactories.actions.executableActions.worker;

import core.Core;
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
		WorkerManagerConstructionJobDistribution workerManagerConstructionJobDistribution = ((PlayerUnitWorker) goapUnit)
				.getWorkerManagerConstructionJobDistribution();
		boolean success = false;

		// Assign the worker to a ConstructionJob if necessary.
		if (!workerManagerConstructionJobDistribution.isAssignedConstructing((PlayerUnitWorker) goapUnit)) {
			success = workerManagerConstructionJobDistribution.addWorker((PlayerUnitWorker) goapUnit);
		}

		// Checked again due to the possibility of the worker not being assigned
		// to a ConstructionJob.
		if (workerManagerConstructionJobDistribution.isAssignedConstructing((PlayerUnitWorker) goapUnit)) {
			IConstrucionInformation constructionInformation = workerManagerConstructionJobDistribution
					.getConstructionInformation((PlayerUnitWorker) goapUnit);

			// Update the ConstructionJob first.
			constructionInformation.update();

			// Walk to the TilePosition the building is being created on when it is undiscovered.
			if(!Core.getInstance().getGame().isExplored(constructionInformation.getTilePosition())) {
				((PlayerUnitWorker) goapUnit).getUnit().move(constructionInformation.getTilePosition().toPosition());
			} 
			// Initiate the construction of the building if necessary.
			else if (!constructionInformation.constructionStarted()) {
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
		if (this.currentlyExecutingUnit != null) {
			WorkerManagerConstructionJobDistribution workerManagerConstructionJobDistribution = ((PlayerUnitWorker) this.currentlyExecutingUnit)
					.getWorkerManagerConstructionJobDistribution();

			if (workerManagerConstructionJobDistribution
					.isAssignedConstructing((PlayerUnitWorker) this.currentlyExecutingUnit)) {
				workerManagerConstructionJobDistribution.removeWorker((PlayerUnitWorker) this.currentlyExecutingUnit);
			}
		}

		this.target = new Object();
	}

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		WorkerManagerConstructionJobDistribution workerManagerConstructionJobDistribution = ((PlayerUnitWorker) goapUnit)
				.getWorkerManagerConstructionJobDistribution();
		
		return workerManagerConstructionJobDistribution.isAssignedConstructing((PlayerUnitWorker) goapUnit)
				|| workerManagerConstructionJobDistribution.canConstruct();
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
		WorkerManagerConstructionJobDistribution workerManagerConstructionJobDistribution = ((PlayerUnitWorker) goapUnit)
				.getWorkerManagerConstructionJobDistribution();

		return workerManagerConstructionJobDistribution.isFinishedConstructing((PlayerUnitWorker) goapUnit);
	}

	// ------------------------------ Getter / Setter

}
