package unitControlModule.actions;

import bwapi.Position;
import bwta.BaseLocation;
import unitControlModule.PlayerUnit;
import unitControlModule.goapActionTaking.GoapState;
import unitControlModule.goapActionTaking.GoapUnit;

/**
 * ScoutBaseLocationsAction.java --- A scouting action for searching a base
 * location
 * 
 * @author P H - 29.01.2017
 *
 */
public class ScoutBaseLocationAction extends ScoutLocationAction {

	/**
	 * @param target type: Position
	 */
	public ScoutBaseLocationAction(Object target) {
		super(target);

		this.addEffect(new GoapState(1, "enemyBuildingsKnown", true));
		this.addPrecondition(new GoapState(1, "enemyBuildingsKnown", false));
	}

	// -------------------- Functions

	@Override
	protected float generateCostRelativeToTarget(GoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().getDistance((Position) this.target);
	}
	
	@Override
	protected boolean isInRange(GoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).isNear(((Position) this.target).toTilePosition(), RANGE_TO_TARGET);
	}
}
