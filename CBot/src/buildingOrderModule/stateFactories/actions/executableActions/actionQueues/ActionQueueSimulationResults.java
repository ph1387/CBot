package buildingOrderModule.stateFactories.actions.executableActions.actionQueues;

import java.util.ArrayList;

import buildingOrderModule.stateFactories.actions.executableActions.ManagerBaseAction;
import javaGOAP.GoapState;

// TODO: UML ADD
/**
 * ActionQueueSimulationResults.java --- ActionQueue designed for enabling the
 * execution of simulation results within the designated BuildActionManager.
 * </br>
 * <b>Notice:</b> </br>
 * The Actions added towards the Queue must be managed and updated by a
 * designated Updater. 
 * 
 * @author P H - 14.07.2017
 *
 */
public class ActionQueueSimulationResults extends ActionQueueDefault {

	/**
	 * @param target
	 *            type: Integer, the amount of times Queue will be iterated
	 *            through.
	 */
	public ActionQueueSimulationResults(Object target) {
		super(target);
		
		this.addPrecondition(new GoapState(0, "simulationAllowed", true));
	}

	// -------------------- Functions

	// ------------------------------ Getter / Setter

	public void setActionQueue(ArrayList<ManagerBaseAction> actionQueue) {
		this.reset();
		
		this.actionQueue = actionQueue;
	}
}
