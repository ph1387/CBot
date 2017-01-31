package unitControlModule.actions;

import bwapi.TilePosition;
import unitControlModule.PlayerUnit;
import unitControlModule.goapActionTaking.GoapState;
import unitControlModule.goapActionTaking.GoapUnit;

/**
 * ScoutBuildingLocationAction.java --- A Scouting action for searching around
 * enemy buildings
 * 
 * @author P H - 30.01.2017
 *
 */
public class ScoutBuildingLocationAction extends ScoutLocationAction {

	/**
	 * @param target
	 *            type: TilePosition
	 */
	public ScoutBuildingLocationAction(Object target) {
		super(target);

		this.addEffect(1, "informationGathering", true);
		this.addPrecondition(new GoapState(1, "enemyBuildingsKnown", true));
	}

	// -------------------- Functions

	@Override
	protected float generateCostRelativeToTarget(GoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().getDistance(((TilePosition) this.target).toPosition());
	}

	@Override
	protected boolean isInRange(GoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).isNear((TilePosition) this.target, RANGE_TO_TARGET);
	}
}
