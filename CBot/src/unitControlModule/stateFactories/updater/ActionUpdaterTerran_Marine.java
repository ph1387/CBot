package unitControlModule.stateFactories.updater;

import unitControlModule.stateFactories.actions.AvailableActionsTerran_Marine;
import unitControlModule.stateFactories.actions.executableActions.AttackUnitActionTerran_Marine_Stimmed;
import unitControlModule.stateFactories.actions.executableActions.RetreatActionSteerInGoalDirectionTerran_Marine_Stimmed;
import unitControlModule.stateFactories.actions.executableActions.abilities.AbilityActionTerranMarine_StimPack;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * ActionUpdaterTerran_Marine.java --- Updater for updating an
 * {@link AvailableActionsTerran_Marine} instance.
 * 
 * @author P H - 23.06.2017
 *
 */
public class ActionUpdaterTerran_Marine extends ActionUpdaterDefault {

	public ActionUpdaterTerran_Marine(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	public void update(PlayerUnit playerUnit) {
		super.update(playerUnit);
		
		// TODO: WIP ONLY DO ONCE!
		((AbilityActionTerranMarine_StimPack) this.getActionFromInstance(AbilityActionTerranMarine_StimPack.class)).setTarget(this.playerUnit);
		
		if(this.playerUnit.currentState == PlayerUnit.UnitStates.ENEMY_KNOWN) {
			((AttackUnitActionTerran_Marine_Stimmed) this.getActionFromInstance(AttackUnitActionTerran_Marine_Stimmed.class)).setTarget(this.playerUnit.getClosestEnemyUnitInConfidenceRange());
			((RetreatActionSteerInGoalDirectionTerran_Marine_Stimmed) this.getActionFromInstance(RetreatActionSteerInGoalDirectionTerran_Marine_Stimmed.class)).setTarget(this.playerUnit.getClosestEnemyUnitInConfidenceRange());
		}
	}
}
