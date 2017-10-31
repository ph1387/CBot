package unitControlModule.stateFactories.actions.executableActions;

import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.stateFactories.actions.executableActions.abilities.AbilityActionTerranSiegeTank_SiegeMode;
import unitControlModule.unitWrappers.PlayerUnitTerran_SiegeTank;

//TODO: UML ADD
/**
 * TerranSiegeTank_TankMode_PrepareBombard.java --- Action for a
 * {@link PlayerUnitTerran_SiegeTank} to prepare the
 * {@link AttackUnitActionTerran_SiegeTank_Bombard} Action. This Action exists
 * only for the "isSieged" state to be included which causes the Unit to morph
 * into Siege_Mode using the {@link AbilityActionTerranSiegeTank_SiegeMode}.
 * This way the Unit can freely morph without having to be in explicit siege
 * range like it is required for the
 * {@link AttackUnitActionTerran_SiegeTank_Bombard} Action.
 * 
 * @author P H - 27.10.2017
 *
 */
public class TerranSiegeTank_TankMode_PrepareBombard extends BaseAction {

	/**
	 * @param target
	 *            type: Null, no target needed since the Unit will morph itself
	 *            with the previous SiegeMode action that is going to be queued
	 *            before this action.
	 */
	public TerranSiegeTank_TankMode_PrepareBombard(Object target) {
		super(new Object());

		this.addEffect(new GoapState(0, "destroyUnit", true));
		this.addPrecondition(new GoapState(0, "allowFighting", true));
		this.addPrecondition(new GoapState(0, "isSieged", true));

		this.addPrecondition(new GoapState(0, "inExpectedSiegeRange", true));
		this.addPrecondition(new GoapState(0, "isExpectingEnemy", true));
	}

	// -------------------- Functions

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		return true;
	}

	@Override
	protected void resetSpecific() {
		this.target = new Object();
	}

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		return true;
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
