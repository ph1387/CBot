package unitControlModule.stateFactories.actions;

import unitControlModule.stateFactories.actions.executableActions.FollowActionTerran_ScienceVessel;
import unitControlModule.stateFactories.actions.executableActions.abilities.AbilityActionTerranScienceVessel_DefensiveMatrix;

/**
 * AvailableActionsTerran_ScienceVessel.java --- HashSet containing all
 * Terran_Science_Vessel Actions.
 * 
 * @author P H - 23.09.2017
 *
 */
public class AvailableActionsTerran_ScienceVessel extends AvailableActionsGeneral {

	public AvailableActionsTerran_ScienceVessel() {
		this.add(new FollowActionTerran_ScienceVessel(null));
		this.add(new AbilityActionTerranScienceVessel_DefensiveMatrix(null));
	}

}
