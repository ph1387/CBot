package unitControlModule.stateFactories.actions.executableActions.worker;

import bwapi.Position;
import bwapi.TilePosition;
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
	 * ConstructBuildingActionWrapper.java --- Wrapper Class used for smartly
	 * moving between ChokePoints.
	 * 
	 * @author P H - 18.03.2018
	 *
	 */
	private class ConstructBuildingActionWrapper implements SmartlyMovingActionWrapper {

		private IConstrucionInformation constructionInformation;

		public ConstructBuildingActionWrapper(IConstrucionInformation constructionInformation) {
			this.constructionInformation = constructionInformation;
		}

		@Override
		public boolean performInternalAction(IGoapUnit goapUnit, Object target) {
			PlayerUnitWorker worker = (PlayerUnitWorker) goapUnit;
			boolean constructionAreaIsUnexplored = false;

			// Update the ConstructionJob first.
			this.constructionInformation.update();

			// Each TilePosition that is used in the construction must be
			// checked since i.e. command centers require a lot of space and
			// only the top left TilePosition might be explored. Nevertheless
			// the building can not be constructed since the rest of the needed
			// TilePosition are still unexplored. Fog of war is okay, not
			// knowing the spot is not!
			for (TilePosition tilePosition : this.constructionInformation.getContendedTilePositions()) {
				if (!Core.getInstance().getGame().isExplored(tilePosition)) {
					constructionAreaIsUnexplored = true;
					break;
				}
			}

			// Walk to the TilePosition the building is being created on when it
			// is unexplored.
			if (constructionAreaIsUnexplored) {
				worker.getUnit().move(constructionInformation.getTilePosition().toPosition());
			} else {
				worker.getUnit().build(this.constructionInformation.getUnitType(),
						this.constructionInformation.getTilePosition());
			}

			// Always return true since the build command might fail i.e. when a
			// Unit is blocking the spot.
			return true;
		}

		@Override
		public Position convertTarget(Object target) {
			return this.constructionInformation.getTilePosition().toPosition();
		}

	}

	private SmartlyMovingActionWrapper actionWrapper;

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
		this.addPrecondition(new GoapState(0, "canConstruct", true));
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

			// Smartly moving part of the action itself:
			try {
				if (this.actionWrapper == null) {
					this.actionWrapper = new ConstructBuildingActionWrapper(constructionInformation);
				}

				this.performSmartlyMovingToRegion(goapUnit, this.actionWrapper);
			} catch (Exception e) {
				e.printStackTrace();
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
		this.actionWrapper = null;
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
