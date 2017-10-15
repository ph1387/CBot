package unitControlModule.stateFactories.actions.executableActions.worker;

import bwapi.Unit;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;
import unitControlModule.unitWrappers.PlayerUnitTerran_SCV;

// TODO: UML ADD
/**
 * RepairAction.java --- Repair action mainly for a {@link PlayerUnitTerran_SCV}
 * since they are able to repair certain Units / buildings.
 * 
 * @author P H - 09.10.2017
 *
 */
public abstract class RepairAction extends WorkerAction {

	/**
	 * @param target
	 *            type: Unit
	 */
	public RepairAction(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "repairing", true));
		this.addPrecondition(new GoapState(0, "isCarryingMinerals", false));
		this.addPrecondition(new GoapState(0, "isCarryingGas", false));
	}

	// -------------------- Functions

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		PlayerUnit playerUnit = (PlayerUnit) goapUnit;
		boolean success = true;

		// Renew the mapping of the repaired Unit from the last iteration since
		// the target of the action may change!
		playerUnit.getInformationStorage().getWorkerConfig().getUnitMapperRepair().mapUnit(playerUnit.getUnit(),
				(Unit) this.target);

		if (this.actionChangeTrigger) {
			success &= ((PlayerUnit) goapUnit).getUnit().repair((Unit) this.target);
		}

		return success;
	}

	@Override
	protected void resetSpecific() {
		// Remove any Units that this one might be repairing from the shared
		// storage.
		if (this.currentlyExecutingUnit != null) {
			PlayerUnit playerUnit = (PlayerUnit) this.currentlyExecutingUnit;
			playerUnit.getInformationStorage().getWorkerConfig().getUnitMapperRepair().unmapUnit(playerUnit.getUnit());
		}
	}

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		return this.target != null && (((PlayerUnit) goapUnit).getUnit().canRepair((Unit) this.target)
				|| ((PlayerUnit) goapUnit).getUnit().isRepairing() || ((Unit) this.target).isBeingHealed());
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return 1;
	}

	@Override
	protected float generateCostRelativeToTarget(IGoapUnit goapUnit) {
		return 0;
	}

	@Override
	protected boolean isDone(IGoapUnit goapUnit) {
		return ((Unit) this.target).getHitPoints() == ((Unit) this.target).getType().maxHitPoints()
				|| !((PlayerUnit) goapUnit).getUnit().canRepair((Unit) this.target);
	}

	// ------------------------------ Getter / Setter

}
