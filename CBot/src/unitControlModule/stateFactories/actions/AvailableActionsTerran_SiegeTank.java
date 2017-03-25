package unitControlModule.stateFactories.actions;

import java.util.HashSet;

import javaGOAP.GoapAction;
import unitControlModule.stateFactories.actions.executableActions.AttackMoveAction;
import unitControlModule.stateFactories.actions.executableActions.AttackUnitAction;
import unitControlModule.stateFactories.actions.executableActions.RetreatActionToFurthestUnitInCone;
import unitControlModule.stateFactories.actions.executableActions.RetreatActionToOwnGatheringPoint;

/**
 * Terran_SiegeTankAvailableActions.java --- HashSet containing all
 * Terran_SiegeTank Actions.
 * 
 * @author P H - 25.03.2017
 *
 */
public class AvailableActionsTerran_SiegeTank extends HashSet<GoapAction> {

	public AvailableActionsTerran_SiegeTank() {
		this.add(new AttackMoveAction(null));
		this.add(new AttackUnitAction(null));
		this.add(new RetreatActionToFurthestUnitInCone(null));
		this.add(new RetreatActionToOwnGatheringPoint(null));
	}
}
