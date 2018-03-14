package buildingOrderModule.stateFactories.actions;

import java.util.LinkedHashSet;

import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionCenter;
import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionRefinery;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionWorker;
import javaGOAP.GoapAction;

// TODO: UML DEPRECATED
@Deprecated
/**
 * AvailableActionsDefault.java --- Default available actions for a
 * BuildActionManager.
 * 
 * @author P H - 28.04.2017
 *
 */
public class AvailableActionsDefault extends LinkedHashSet<GoapAction> {

	public AvailableActionsDefault() {
		this.add(new TrainUnitActionWorker(1));
		this.add(new ConstructActionCenter(1));
		this.add(new ConstructActionRefinery(1));
	}
}
