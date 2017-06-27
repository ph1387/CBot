package unitControlModule.stateFactories.actions;

import java.util.HashSet;

import javaGOAP.GoapAction;
import unitControlModule.stateFactories.actions.executableActions.RetreatActionSteerInGoalDirection;

// TODO: UML ADD
/**
 * AvailableActionsTerran_Medic.java --- HashSet containing all Terran_Medic
 * Actions.
 * 
 * @author P H - 27.06.2017
 *
 */
public class AvailableActionsTerran_Medic extends HashSet<GoapAction> {

	public AvailableActionsTerran_Medic() {
		this.add(new RetreatActionSteerInGoalDirection(null));
	}
}
