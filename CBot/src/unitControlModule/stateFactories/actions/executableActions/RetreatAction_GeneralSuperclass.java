package unitControlModule.stateFactories.actions.executableActions;

import java.util.HashSet;

import bwapi.Color;
import bwapi.Position;
import core.Core;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * RetreatAction_GeneralSuperclass.java --- Superclass for RetreatActions.
 * 
 * @author P H - 10.03.2017
 *
 */
public abstract class RetreatAction_GeneralSuperclass extends BaseAction {
	private static final int DIST_TO_GATHERING_POINT = Core.getInstance().getTileSize();
	protected static final int TILE_RADIUS_NEAR = 1;
	// Has to be larger than DIST_TO_GATHERING_POINT
	// -> isDone() condition! v
	protected static final int MIN_PIXELDISTANCE_TO_UNIT = 320; // 240
	protected static final int MAX_PIXELDISTANCE_TO_UNIT = 20 * Core.getInstance().getTileSize(); // 15

	protected static HashSet<Position> gatheringPoints = new HashSet<Position>();

	protected Position generatedTempRetreatPosition = null;
	protected Position retreatPosition = null;

	/**
	 * @param target
	 *            type: Unit
	 */
	public RetreatAction_GeneralSuperclass(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "retreatFromUnit", true));
		this.addPrecondition(new GoapState(0, "enemyKnown", true));
	}

	// -------------------- Functions

	@Override
	protected boolean isDone(IGoapUnit goapUnit) {
		if (((PlayerUnit) goapUnit).isNearPosition(this.retreatPosition, DIST_TO_GATHERING_POINT)) {
			RetreatAction_GeneralSuperclass.gatheringPoints.remove(this.retreatPosition);
		}

		return !RetreatAction_GeneralSuperclass.gatheringPoints.contains(this.retreatPosition);
	}

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		boolean success = true;

		// TODO: DEBUG INFO
		// // Position to which the Unit retreats to
		Core.getInstance().getGame().drawLineMap(((PlayerUnit) goapUnit).getUnit().getPosition(), this.retreatPosition,
				new Color(255, 255, 0));

		// Only override the current retreatPosition if the action trigger is
		// set and move towards it. This enables the ability of storing the
		// Positions inside a HashSet and moving other Units towards them
		// instead of constantly updating them, which would result in a Unit
		// only following them in one general direction. Once a Position is set,
		// stick with it.
		if (this.actionChangeTrigger && this.generatedTempRetreatPosition != null) {
			this.retreatPosition = this.generatedTempRetreatPosition;
			RetreatAction_GeneralSuperclass.gatheringPoints.add(this.retreatPosition);
			success &= this.retreatPosition != null && ((PlayerUnit) goapUnit).getUnit().move(this.retreatPosition);
		} else if (this.actionChangeTrigger && this.generatedTempRetreatPosition == null) {
			success = false;
		}

		if (this.retreatPosition != null) {

			// TODO: DEBUG INFO
			// // Position to which the Unit retreats to
			Core.getInstance().getGame().drawLineMap(((PlayerUnit) goapUnit).getUnit().getPosition(),
					this.retreatPosition, new Color(255, 255, 0));

		}
		return this.retreatPosition != null && success;
	}

	@Override
	protected float generateCostRelativeToTarget(IGoapUnit goapUnit) {
		float returnValue = 0.f;

		try {
			returnValue = ((PlayerUnit) goapUnit).getUnit().getDistance(this.generatedTempRetreatPosition);
		} catch (Exception e) {
			returnValue = Float.MAX_VALUE;
		}

		return returnValue;
	}

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		boolean success = false;

		if (this.target != null && ((PlayerUnit) goapUnit).getNearestEnemyUnitInConfidenceRange() != null) {
			success = this.checkProceduralSpecificPrecondition(goapUnit);

			// The first ever found Position has to be added as temp retreat
			// Position. This ensures, that isDone() returns false and the
			// action
			// gets actually executed. The actual retreatPosition gets set when
			// performAction() gets called.
			if (this.retreatPosition == null) {
				this.retreatPosition = this.generatedTempRetreatPosition;
				RetreatAction_GeneralSuperclass.gatheringPoints.add(this.generatedTempRetreatPosition);
			}
		}
		return success;
	}

	/**
	 * Each Subclass implements its own specific precondition test.
	 * 
	 * @param goapUnit
	 *            the Unit the test is performed on.
	 * @return true or false depending if the test was successful or not.
	 */
	protected abstract boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit);

	@Override
	protected boolean requiresInRange(IGoapUnit goapUnit) {
		return false;
	}

	@Override
	protected boolean isInRange(IGoapUnit goapUnit) {
		return false;
	}

	@Override
	protected void reset() {
		this.retreatPosition = null;
		this.resetStoredAction();
	}
}
