package unitControlModule.stateFactories.actions;

import unitControlModule.stateFactories.actions.executableActions.FollowActionTerran_Medic;
import unitControlModule.stateFactories.actions.executableActions.abilities.AbilityActionTerranMedic_Heal;

/**
 * AvailableActionsTerran_Medic.java --- HashSet containing all Terran_Medic
 * Actions.
 * 
 * @author P H - 27.06.2017
 *
 */
public class AvailableActionsTerran_Medic extends AvailableActionsGeneral {

	public AvailableActionsTerran_Medic() {
		this.add(new AbilityActionTerranMedic_Heal(null));
		this.add(new FollowActionTerran_Medic(null));
	}
}
