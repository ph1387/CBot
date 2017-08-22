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

		this.addEffect(new GoapState(0, "enemyKnown", true));
		this.addPrecondition(new GoapState(0, "enemyKnown", false));
		this.addPrecondition(new GoapState(0, "isScout", true));
		this.addPrecondition(new GoapState(0, "canMove", true));
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
		return 10;
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

	// -------------------- Group

	@Override
	public boolean canPerformGrouped() {
		// All scouting actions are executed by a single Unit. It makes no
		// difference if one or more Units move towards the same location. It
		// can only be discovered once!
		return false;
	}

	@Override
	public boolean performGrouped(IGoapUnit groupLeader, IGoapUnit groupMember) {
		return false;
	}
	
	// TODO: UML ADD
	@Override
	public int defineMaxGroupSize() {
		return 0;
	}
	
	// TODO: UML ADD
	@Override
	public int defineMaxLeaderTileDistance() {
		return 0;
	}
	
}
