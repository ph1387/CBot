package unitControlModule.goapActionTaking;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;

public abstract class GoapUnit {
	/**
	 * GoapUnit.java --- The Superclass for a unit using the GoapAgent
	 * @author P H - 28.01.2017
	 */
	
	private List<GoapState> goalState;
	private HashSet<GoapState> worldState;
	private HashSet<GoapAction> availableActions;
	
	private List<Object> importantUnitGoalChangeListeners = new ArrayList<Object>();
	
	public GoapUnit() {
		
	}
	
	// -------------------- Functions
	
	/**
	 * Gets called when a plan was found by the planner.
	 * 
	 * @param goal the goal the unit tries to archive.
	 * @param actions the actions the unit hat to take in order to archive the goal.
	 */
	protected abstract void goapPlanFound(Queue<GoapAction> actions);
	
	/**
	 * Gets called when a plan failed to execute. 
	 *
	 * @param goal the goal the unit tried to archive.
	 */
	protected abstract void goapPlanFailed(Queue<GoapAction> actions);
	
	/**
	 * Gets called when a plan was finished.
	 */
	protected abstract void goapPlanFinished();
	
	/**
	 * General update from the Agent. Called in a loop until the program ends.
	 */
	protected abstract void update();
	
	/**
	 * Function to move to a specific location. Gets called by the moveToState when the unit has to move to a certain target.
	 *
	 * @param target the target the unit has to move to.
	 * @return true or false depending if the unit was able to move.
	 */
	protected abstract boolean moveTo(Object target);
	
	// ------------------------------ Getter / Setter
	
	// ---------------------------------------- WorldState
	protected void setWorldState(HashSet<GoapState> worldState) {
		this.worldState = worldState;
	}
	
	protected void addWorldState(GoapState effect) {
		boolean missing = true;
		
		for (GoapState state : this.worldState) {
			if(effect.equals(state.effect)) {
				missing = false;
				
				break;
			}
		}

		if(missing) {
			this.worldState.add(effect);
		}
	}
	
	protected void removeWorldState(String effect) {
		GoapState marked = null;
		
		for (GoapState state : this.worldState) {
			if(effect.equals(state.effect)) {
				marked = state;
				
				break;
			}
		}
		
		if(marked != null) {
			this.worldState.remove(marked);
		}
	}
	
	protected void removeWorldState(GoapState effect) {
		this.worldState.remove(effect);
	}
	
	protected HashSet<GoapState> getWorldState() {
		return this.worldState;
	}
	
	// ---------------------------------------- GoalState
	protected void setGoalState(ArrayList<GoapState> goalState) {
		this.goalState = goalState;
	}
	
	protected void addGoalState(GoapState effect) {
		boolean missing = true;
		
		for (GoapState state : this.goalState) {
			if(effect.equals(state.effect)) {
				missing = false;
				
				break;
			}
		}

		if(missing) {
			this.goalState.add(effect);
		}
	}
	
	protected void removeGoalState(String effect) {
		GoapState marked = null;
		
		for (GoapState state : this.goalState) {
			if(effect.equals(state.effect)) {
				marked = state;
				
				break;
			}
		}
		
		if(marked != null) {
			this.goalState.remove(marked);
		}
	}
	
	protected void removeGoalStat(GoapState effect) {
		this.goalState.remove(effect);
	}
	
	protected List<GoapState> getGoalState() {
		return this.goalState;
	}
	
	// ---------------------------------------- Available Actions
	protected void setAvailableActions(HashSet<GoapAction> availableActions) {
		this.availableActions = availableActions;
	}
	
	protected void addAvailableAction(GoapAction action) {
		if(!this.availableActions.contains(action)) {
			this.availableActions.add(action);
		}
	}
	
	protected void removeAvailableAction(GoapAction action) {
		this.availableActions.remove(action);
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
