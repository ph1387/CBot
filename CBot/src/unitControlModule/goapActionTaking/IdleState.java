package unitControlModule.goapActionTaking;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

class IdleState implements IFSMState {

	private List<Object> planCreatedListeners = new ArrayList<Object>();
	
	public IdleState() {
		
	}
	
	// -------------------- Functions
	
	@Override
	public boolean runGoapAction(GoapUnit goapUnit) {
		Queue<GoapAction> plannedQueue = GoapPlanner.plan(goapUnit);
		
		if(plannedQueue != null) {
			this.dispatchNewPlanCreatedEvent(plannedQueue);
			
			return false;
		} else {
			return true;
		}
	}
	
	// -------------------- Events
	
	// ------------------------------ Plan created
	synchronized void addPlanCreatedListener(Object listener) {
		this.planCreatedListeners.add(listener);
	}
	
	synchronized void removePlanCreatedListener(Object listener) {
		this.planCreatedListeners.remove(listener);
	}
	
	private synchronized void dispatchNewPlanCreatedEvent(Queue<GoapAction> plan) {
		for (Object listener : this.planCreatedListeners) {
			((PlanCreatedEventListener)listener).onPlanCreated(plan);
		}
	}
}
