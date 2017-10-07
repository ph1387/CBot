package unitControlModule.stateFactories.actions;

import unitControlModule.stateFactories.actions.executableActions.abilities.AbilityActionTerranWraith_Cloak;
import unitControlModule.stateFactories.actions.executableActions.abilities.AbilityActionTerranWraith_Decloak;

/**
 * AvailableActionsTerran_Wraith.java --- HashSet containing all Terran_Wraith
 * Actions.
 * 
 * @author P H - 13.09.2017
 *
 */
public class AvailableActionsTerran_Wraith extends AvailableActionsDefault {

	public AvailableActionsTerran_Wraith() {
		this.add(new AbilityActionTerranWraith_Cloak(null));
		this.add(new AbilityActionTerranWraith_Decloak(null));
	}
}
