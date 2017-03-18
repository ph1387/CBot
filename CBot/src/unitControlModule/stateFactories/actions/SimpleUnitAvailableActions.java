package unitControlModule.stateFactories.actions;

import java.util.HashSet;

import javaGOAP.GoapAction;
import unitControlModule.stateFactories.actions.executableActions.AttackMoveAction;
import unitControlModule.stateFactories.actions.executableActions.AttackUnitAction;
import unitControlModule.stateFactories.actions.executableActions.RetreatAction_ToFurthestUnitInCone;
import unitControlModule.stateFactories.actions.executableActions.RetreatAction_ToOwnGatheringPoint;
import unitControlModule.stateFactories.actions.executableActions.ScoutBaseLocationAction;

/**
 * SimpleUnitAvailableActions.java --- A simple HashSet for a Unit containing
 * all basic Actions.
 * 
 * @author P H - 26.02.2017
 *
 */
public class SimpleUnitAvailableActions extends HashSet<GoapAction> {

	public SimpleUnitAvailableActions() {
		this.add(new ScoutBaseLocationAction(null));
		this.add(new AttackMoveAction(null));
		this.add(new AttackUnitAction(null));
//		this.add(new RetreatAction_ToFurthestUnitInCone(null));
		this.add(new RetreatAction_ToOwnGatheringPoint(null));
	}
}
