package unitControlModule.stateFactories.actions.executableActions;

import core.Core;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnitTerran_SiegeTank;
import unitControlModule.unitWrappers.PlayerUnitTerran_SiegeTank_SiegeMode;

//TODO: UML ADD
/**
 * TerranSiegeTank_SiegeMode_WaitForExpectedEnemy.java --- Action for a
 * {@link PlayerUnitTerran_SiegeTank_SiegeMode} to wait for an expected enemy in
 * Siege_Mode. The Action is done if an enemy Unit moves into siege range, the
 * executing Unit is no longer expecting another enemy one or it expires after a
 * certain period of time.
 * 
 * @author P H - 27.10.2017
 *
 */
public class TerranSiegeTank_SiegeMode_WaitForExpectedEnemy extends BaseAction {

	private int frameTimeWait = 160;
	private Integer frameTimeStampStart = null;

	// The minimum number of frames that must pass before this action can be
	// executed again. This prevents the Unit from waiting permanently and not
	// morphing back to Tank_Mode.
	private int frameTimeWaitBetweenIterations = 72;
	private Integer frameTimeStampLastIteration = null;

	/**
	 * @param target
	 *            type: Null, no target needed since the Unit will wait until
	 *            certain conditions are met.
	 */
	public TerranSiegeTank_SiegeMode_WaitForExpectedEnemy(Object target) {
		super(new Object());

		// Enemy can be unknown since the Unit is actively waiting for one!
		this.addEffect(new GoapState(0, "destroyUnit", true));
		this.addPrecondition(new GoapState(0, "allowFighting", true));
		this.addPrecondition(new GoapState(0, "isSieged", true));

		// Must NOT (!) already be in siege range. This prevents Units that are
		// already near enemy Units from using this action!
		this.addPrecondition(new GoapState(0, "inSiegeRange", false));
		this.addPrecondition(new GoapState(0, "belowSiegeRange", false));
		this.addPrecondition(new GoapState(0, "isExpectingEnemy", true));
	}

	// -------------------- Functions

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		if (this.frameTimeStampStart == null) {
			this.frameTimeStampStart = Core.getInstance().getGame().getFrameCount();
		}

		return true;
	}

	@Override
	protected void resetSpecific() {
		this.target = new Object();
		this.frameTimeStampStart = null;
	}

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		int frameCount = Core.getInstance().getGame().getFrameCount();
		boolean wasNotExecutedBefore = this.frameTimeStampLastIteration == null;
		boolean canBeExecutedAgain = this.frameTimeStampLastIteration != null
				&& frameCount - this.frameTimeStampLastIteration >= this.frameTimeWaitBetweenIterations;
		boolean isCurrentlyBeingExecuted = this.frameTimeStampStart != null
				&& frameCount - this.frameTimeStampStart < this.frameTimeWait;

		return ((PlayerUnitTerran_SiegeTank) goapUnit).isExpectingEnemy()
				&& (wasNotExecutedBefore || canBeExecutedAgain || isCurrentlyBeingExecuted);
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
		PlayerUnitTerran_SiegeTank siegeTank = ((PlayerUnitTerran_SiegeTank) goapUnit);
		boolean noLongerExpectingEnemy = !siegeTank.isExpectingEnemy();
		boolean enemyInRange = siegeTank.getAttackableEnemyUnitToReactTo() != null
				&& (siegeTank.isInSiegeRange(siegeTank.getAttackableEnemyUnitToReactTo())
						|| siegeTank.isBelowSiegeRange(siegeTank.getAttackableEnemyUnitToReactTo()));
		boolean maxWaitTimePassed = this.frameTimeStampStart != null
				&& Core.getInstance().getGame().getFrameCount() - this.frameTimeStampStart >= this.frameTimeWait;
		boolean isBeingAttacked = siegeTank.getUnit().isUnderAttack();
		boolean isDone = noLongerExpectingEnemy || enemyInRange || maxWaitTimePassed || isBeingAttacked;

		if (isDone) {
			this.frameTimeStampLastIteration = Core.getInstance().getGame().getFrameCount();
		}
		return isDone;
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
