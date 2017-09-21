package unitControlModule.stateFactories.actions.executableActions;

import bwapi.Unit;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;
import unitControlModule.unitWrappers.PlayerUnitTerran_Medic;

// TODO: UML ADD
/**
 * FollowActionTerran_Medic.java --- A follow action for
 * {@link PlayerUnitTerran_Medic}s with which the Unit is able to follow another
 * one around (Mainly Terran_Marines and Terran_Firebats). It is necessary to
 * define an extra Action for this kind of behavior since the Medics would
 * otherwise simply wait around until another Unit on the map gets hurt. Then
 * and only then they would walk towards that specific Unit, which in most cases
 * is too late. With this the medics are always close to the Units that might
 * require healing.
 * 
 * @author P H - 19.09.2017
 *
 */
public class FollowActionTerran_Medic extends BaseAction {

	/**
	 * @param target
	 *            type: Unit
	 */
	public FollowActionTerran_Medic(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "isNearHealableUnit", true));
		this.addPrecondition(new GoapState(0, "isNearHealableUnit", false));
		this.addPrecondition(new GoapState(0, "canMove", true));
	}

	// -------------------- Functions

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().follow((Unit) this.target);
	}

	@Override
	protected void resetSpecific() {

	}

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		return this.target != null && ((PlayerUnit) goapUnit).getUnit().canFollow((Unit) this.target);
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return 1.f;
	}

	@Override
	protected float generateCostRelativeToTarget(IGoapUnit goapUnit) {
		return 0;
	}

	@Override
	protected boolean isDone(IGoapUnit goapUnit) {
		return this.target == null || (this.target != null && ((PlayerUnit) goapUnit)
				.isNearPosition(((Unit) this.target).getPosition(), PlayerUnitTerran_Medic.getHealPixelDistance()));
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
