package buildingOrderModule.stateFactories;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.stateFactories.updater.Updater;
import javaGOAP.GoapAction;
import javaGOAP.GoapState;

public interface StateFactory {
	/**
	 * @return a Object which represents the current WorldState.
	 */
	public HashSet<GoapState> generateWorldState();

	/**
	 * @return a Object which represents the current GoalState.
	 */
	public List<GoapState> generateGoalState();

	// TODO: UML RETURN TYPE
	/**
	 * @return a Object which represents the currently available Actions. </br>
	 *         <b>Note:</b></br>
	 *         The return type is a LinkedHashSet due to the order of upgrades
	 *         and technologies being important. This is the main difference in
	 *         comparison with the
	 *         {@link unitControlModule.stateFactories.StateFactory} class.
	 */
	public LinkedHashSet<GoapAction> generateAvailableActions();

	/**
	 * @param manager
	 *            the BuildActionManager which the factory is being assigned to.
	 * @return a Updater matching the used WorldState.
	 */
	public Updater getMatchingWorldStateUpdater(BuildActionManager manager);

	/**
	 * @param manager
	 *            the BuildActionManager which the factory is being assigned to.
	 * @return a Updater matching the used GoalState.
	 */
	public Updater getMatchingGoalStateUpdater(BuildActionManager manager);

	/**
	 * @param manager
	 *            the BuildActionManager which the factory is being assigned to.
	 * @return a Updater matching the assigned Actions.
	 */
	public Updater getMatchingActionUpdater(BuildActionManager manager);
}
