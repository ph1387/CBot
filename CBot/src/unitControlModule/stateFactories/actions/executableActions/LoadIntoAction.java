package unitControlModule.stateFactories.actions.executableActions;

import bwapi.Unit;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

// TODO: UML ADD
/**
 * LoadIntoAction.java --- A loading action with which a Unit can "load itself"
 * into another one. The BWAPI command that is used here is the "load" one,
 * which the loading Unit must perform in order to load the executing one.
 * Therefore the direction of this action can be seen as "reversed" as the
 * actually acting Unit is not the executing but the targeted loading one.
 * 
 * @author P H - 13.05.2018
 *
 */
public abstract class LoadIntoAction extends BaseAction {

	// The Unit the executing one is loaded into. This Unit will perform the
	// "load" BWAPI command.
	protected Unit loadingUnit;

	/**
	 * @param target
	 *            type: Null
	 */
	public LoadIntoAction(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "isLoaded", true));
	}

	// -------------------- Functions

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		boolean success = this.loadingUnit != null;

		// Ensure that a custom loading Unit is selected in which the executing
		// one is being loaded into.
		if (this.loadingUnit == null) {
			this.loadingUnit = this.defineLoadingUnit(goapUnit);
		}

		if (this.loadingUnit != null && this.actionChangeTrigger) {
			success = this.loadingUnit.load(((PlayerUnit) goapUnit).getUnit());
		}

		return success;
	}

	@Override
	protected void resetSpecific() {
		this.loadingUnit = null;
		this.target = new Object();
	}

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		Unit unit = ((PlayerUnit) goapUnit).getUnit();
		boolean success = this.isExecutionPossible(goapUnit) && !unit.isLoaded();

		// Check while executing if loading is still possible. This is needed
		// since the Unit the executing one is loaded into could i.e. die.
		if (this.loadingUnit != null && this.loadingUnit.canLoad() && this.loadingUnit.canLoad(unit)) {
			success &= this.loadingUnit.exists() && this.loadingUnit.getSpaceRemaining() > 0;
		}

		return success;
	}

	// TODO: UML ADD
	/**
	 * Function for determining if an execution is possible. This check is
	 * performed each time
	 * {@link LoadIntoAction#checkProceduralPrecondition(IGoapUnit)} is called.
	 * Therefore this function must take planning as well as executing into
	 * account.
	 * 
	 * @param goapUnit
	 *            the executing Unit.
	 * @return true if an execution of the action is possible.
	 */
	protected abstract boolean isExecutionPossible(IGoapUnit goapUnit);

	// TODO: UML ADD
	/**
	 * Function for defining the Unit that the executing one will be loaded
	 * into. The Unit returned here will be ordered to load the executing one
	 * into itself with the corresponding BWAPI command.
	 * 
	 * @param goapUnit
	 *            the executing Unit.
	 * @return the Unit that the executing one will be loaded into.
	 */
	protected abstract Unit defineLoadingUnit(IGoapUnit goapUnit);

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return 1;
	}

	@Override
	protected float generateCostRelativeToTarget(IGoapUnit goapUnit) {
		float cost = 0.f;

		if (this.loadingUnit != null) {
			cost = ((PlayerUnit) goapUnit).getUnit().getDistance(this.loadingUnit);
		}

		return cost;
	}

	@Override
	protected boolean isDone(IGoapUnit goapUnit) {
		PlayerUnit playerUnit = (PlayerUnit) goapUnit;
		// The Unit the executing one is loaded into does not matter since
		// two loading actions are not queued together.
		boolean unitSuccessfullyLoaded = playerUnit.getUnit().isLoaded();
		boolean done = unitSuccessfullyLoaded;

		if (this.loadingUnit != null) {
			boolean loadingUnitDied = !this.loadingUnit.exists();
			boolean noSpace = this.loadingUnit.getSpaceRemaining() == 0;
			boolean cantLoadUnit = !this.loadingUnit.canLoad() || !this.loadingUnit.canLoad(playerUnit.getUnit());

			done = unitSuccessfullyLoaded || noSpace || loadingUnitDied || cantLoadUnit;
		}

		return done;
	}

	@Override
	protected boolean isInRange(IGoapUnit goapUnit) {
		return false;
	}

	@Override
	protected boolean requiresInRange(IGoapUnit goapUnit) {
		return false;
	}

	// -------------------- Group

	@Override
	public boolean canPerformGrouped() {
		return false;
	}

	@Override
	public boolean performGrouped(IGoapUnit groupLeader, IGoapUnit groupMember) {
		return false;
	}

	@Override
	public int defineMaxGroupSize() {
		return 0;
	}

	@Override
	public int defineMaxLeaderTileDistance() {
		return 0;
	}

}
