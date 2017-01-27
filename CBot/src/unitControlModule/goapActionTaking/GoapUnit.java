package unitControlModule.goapActionTaking;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;

public abstract class GoapUnit {
	
	private List<GoapState> goalState;
	private HashSet<GoapState> worldState;
	private HashSet<GoapAction> availableActions;
	
	private List<Object> importantUnitGoalChangeListeners = new ArrayList<Object>();
	
	public GoapUnit() {
		
	}
	
	// -------------------- Functions
	
	protected abstract void goapPlanFound(GoapState goal, Queue<GoapAction> actions);
	
	protected abstract void goapPlanFailed(GoapState goal);
	
	protected abstract void goapPlanFinished();
	
	protected abstract void update();
	
	// ------------------------------ Getter / Setter
	
	protected void setWorldState(HashSet<GoapState> worldState) {
		this.worldState = worldState;		// TODO: Simplify the function call
	}
	
	protected void setGoalState(ArrayList<GoapState> goalState) {
		this.goalState = goalState;		// TODO: Simplify the function call
	}
	
	protected void setAvailableActions(HashSet<GoapAction> availableActions) {
		this.availableActions = availableActions;		// TODO: Simplify the function call
	}
	
	protected HashSet<GoapState> getWorldState() {
		return this.worldState;
	}
	
	protected List<GoapState> getGoalState() {
		return this.goalState;
	}
	
	protected HashSet<GoapAction> getAvailableActions() {
		return this.availableActions;
	}
	
	// -------------------- Events
	
	// ------------------------------ Important unit goal changes
	synchronized void addImportantUnitGoalChangeListener(Object listener) {
		this.importantUnitGoalChangeListeners.add(listener);
	}
	
	synchronized void removeImportantUnitGoalChangeListener(Object listener) {
		this.importantUnitGoalChangeListeners.remove(listener);
	}
	
	protected synchronized void dispatchNewImportantUnitGoalChangeEvent(GoapState newGoalState) {
		for (Object listener : this.importantUnitGoalChangeListeners) {
			((ImportantUnitGoalChangeEventListener)listener).onImportantUnitGoalChange(newGoalState);
		}
	}
}
