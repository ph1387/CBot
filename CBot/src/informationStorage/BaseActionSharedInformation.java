package informationStorage;

import java.util.HashMap;

import unitControlModule.stateFactories.actions.executableActions.BaseAction;
import unitControlModule.stateFactories.actions.executableActions.grouping.GroupActionManager;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * BaseActionSharedInformation.java --- Class for sharing information between
 * all instances or subclasses of {@link BaseAction} instances. This Class is
 * needed due to the amount of Classes that share this information and the
 * number of constructors that would have to be changed in order to achieve the
 * desired effect of the instances in this Class being shared between all
 * instances of {@link BaseAction}s.
 * 
 * @author P H - 17.11.2017
 *
 */
public class BaseActionSharedInformation {

	private HashMap<PlayerUnit, BaseAction> currentlyExecutingActions = new HashMap<>();
	private GroupActionManager groupActionManager = new GroupActionManager();

	public BaseActionSharedInformation() {

	}

	// -------------------- Functions

	// ------------------------------ Getter / Setter

	public HashMap<PlayerUnit, BaseAction> getCurrentlyExecutingActions() {
		return this.currentlyExecutingActions;
	}

	public GroupActionManager getGroupActionManager() {
		return this.groupActionManager;
	}

}
