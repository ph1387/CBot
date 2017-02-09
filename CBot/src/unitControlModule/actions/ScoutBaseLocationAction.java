package unitControlModule.actions;

import bwapi.Position;
import bwta.BaseLocation;
import unitControlModule.PlayerUnit;
import unitControlModule.goapActionTaking.GoapAction;
import unitControlModule.goapActionTaking.GoapState;
import unitControlModule.goapActionTaking.GoapUnit;

/**
 * ScoutBaseLocationsAction.java --- A scouting action for searching a base
 * location.
 * 
 * @author P H - 29.01.2017
 *
 */
public class ScoutBaseLocationAction extends BaseAction {

	protected static Integer RANGE_TO_TARGET = null;

	/**
	 * @param target
	 *            type: Position
	 */
	public ScoutBaseLocationAction(Object target) {
		super(target);

		this.addEffect(new GoapState(1, "enemyKnown", true));
		this.addPrecondition(new GoapState(1, "enemyKnown", false));
	}

	// -------------------- Functions

	@Override
	protected boolean isDone(GoapUnit goapUnit) {
		return this.isInRange(goapUnit);
	}

	@Override
	protected boolean performAction(GoapUnit goapUnit) {
		return true;
	}

	@Override
	protected float generateBaseCost(GoapUnit goapUnit) {
		return 0;
	}

	@Override
	protected boolean checkProceduralPrecondition(GoapUnit goapUnit) {
		return this.target != null;
	}

	@Override
	protected boolean requiresInRange(GoapUnit goapUnit) {
		return true;
	}

	@Override
	protected float generateCostRelativeToTarget(GoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().getDistance((Position) this.target);
	}

	@Override
	protected boolean isInRange(GoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).isNear(((Position) this.target).toTilePosition(), RANGE_TO_TARGET);
	}
}
