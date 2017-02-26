package unitControlModule.stateFactories;

import java.util.HashSet;
import java.util.List;

import unitControlModule.goapActionTaking.GoapAction;
import unitControlModule.goapActionTaking.GoapState;
import unitControlModule.stateFactories.actions.executableActions.BaseAction;
import unitControlModule.stateFactories.updater.Updater;
import unitControlModule.unitWrappers.PlayerUnit;

public interface StateFactory {
	/**
	 * @return a Object which represents the current WorldState.
	 */
	public HashSet<GoapState> generateWorldState();
	
	/**
	 * @return a Object which represents the current GoalState.
	 */
	public List<GoapState> generateGoalState();
	
	/**
	 * @return a Object which represents the currently available Actions.
	 */
	public HashSet<GoapAction> generateAvailableActions();
	
	/**
	 * @param playerUnit the PlayerUnit which the factory is assigned to.
	 * @return a Updater matching the used WorldState.
	 */
	public Updater getMatchingWorldStateUpdater(PlayerUnit playerUnit);
	
	/**
	 * @param playerUnit the PlayerUnit which the factory is assigned to.
	 * @return a Updater matching the used GoalState.
	 */
	public Updater getMatchingGoalStateUpdater(PlayerUnit playerUnit);
	
	/**
	 * @param playerUnit the PlayerUnit which the factory is assigned to.
	 * @return a Updater matching the assigned Actions.
	 */
	public Updater getMatchingActionUpdater(PlayerUnit playerUnit);
}
