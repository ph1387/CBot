package unitControlModule.stateFactories.actions.executableActions;

import bwapi.Position;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

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
		this.addPrecondition(new GoapState(1, "isScout", true));
	}

	// -------------------- Functions

	@Override
	protected boolean isDone(IGoapUnit goapUnit) {
		return this.isInRange(goapUnit);
	}

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		return true;
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return 0;
	}

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		return this.target != null;
	}

	@Override
	protected boolean requiresInRange(IGoapUnit goapUnit) {
		return true;
	}

	@Override
	protected float generateCostRelativeToTarget(IGoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().getDistance((Position) this.target);
	}

	@Override
	protected boolean isInRange(IGoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).isNearTilePosition(((Position) this.target).toTilePosition(), RANGE_TO_TARGET);
	}

	@Override
	protected void resetSpecific() {
		
	}
}
