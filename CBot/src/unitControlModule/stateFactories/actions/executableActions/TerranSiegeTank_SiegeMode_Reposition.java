package unitControlModule.stateFactories.actions.executableActions;

import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnitTerran_SiegeTank_SiegeMode;

/**
 * TerranSiegeTank_SiegeMode_Reposition.java --- Action for a
 * {@link PlayerUnitTerran_SiegeTank_SiegeMode} to reposition itself. This is
 * necessary since otherwise the Unit would stay in SiegeMode since no other
 * goal can be reached.
 * 
 * @author P H - 01.09.2017
 *
 */
public class TerranSiegeTank_SiegeMode_Reposition extends BaseAction {

	/**
	 * @param target
	 *            type: Null, no target needed since the Unit will morph itself
	 *            with the previous TankMode action that is going to be queued
	 *            before this action.
	 */
	public TerranSiegeTank_SiegeMode_Reposition(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "destroyUnit", true));
		this.addPrecondition(new GoapState(0, "isSieged", false));
		this.addPrecondition(new GoapState(0, "canMove", true));
	}

	// -------------------- Functions

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		return true;
	}

	@Override
	protected void resetSpecific() {

	}

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		return true;
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		// Must be more expensive than the bombard attack action since
		// performing this action chain the Unit will morph back to TankMode to
		// reposition itself.
		return 2f;
	}

	@Override
	protected float generateCostRelativeToTarget(IGoapUnit goapUnit) {
		return 0;
	}

	@Override
	protected boolean isDone(IGoapUnit goapUnit) {
		return true;
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
