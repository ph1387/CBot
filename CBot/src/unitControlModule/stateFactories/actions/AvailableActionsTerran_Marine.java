package unitControlModule.stateFactories.actions;

import unitControlModule.stateFactories.actions.executableActions.AttackUnitActionTerran_Marine_Stimmed;
import unitControlModule.stateFactories.actions.executableActions.LoadIntoActionTerran_Bunker;
import unitControlModule.stateFactories.actions.executableActions.RetreatActionSteerInRetreatVectorDirectionTerran_Marine_Stimmed;
import unitControlModule.stateFactories.actions.executableActions.abilities.AbilityActionTerranMarine_StimPack;

/**
 * AvailableActionsTerran_Marine.java --- HashSet containing all Terran_Marine
 * Actions.
 * 
 * @author P H - 23.06.2017
 *
 */
public class AvailableActionsTerran_Marine extends AvailableActionsDefault {

	public AvailableActionsTerran_Marine() {
		this.add(new AbilityActionTerranMarine_StimPack(null));
		this.add(new AttackUnitActionTerran_Marine_Stimmed(null));
		this.add(new RetreatActionSteerInRetreatVectorDirectionTerran_Marine_Stimmed(null));
		this.add(new LoadIntoActionTerran_Bunker(null));
	}
}
