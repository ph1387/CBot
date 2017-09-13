package unitControlModule.stateFactories.updater;

import unitControlModule.stateFactories.actions.AvailableActionsTerran_Marine;
import unitControlModule.stateFactories.actions.executableActions.AttackUnitActionTerran_Marine_Stimmed;
import unitControlModule.stateFactories.actions.executableActions.RetreatActionSteerInRetreatVectorDirectionTerran_Marine_Stimmed;
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

	private AbilityActionTerranMarine_StimPack abilityActionTerranMarine_StimPack;
	private AttackUnitActionTerran_Marine_Stimmed attackUnitActionTerran_Marine_Stimmed;
	private RetreatActionSteerInRetreatVectorDirectionTerran_Marine_Stimmed retreatActionSteerInRetreatVectorDirectionTerran_Marine_Stimmed;

	public ActionUpdaterTerran_Marine(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	public void update(PlayerUnit playerUnit) {
		super.update(playerUnit);

		// TODO: Possible Change: Only perform once.
		this.abilityActionTerranMarine_StimPack.setTarget(this.playerUnit);

		if (this.playerUnit.currentState == PlayerUnit.UnitStates.ENEMY_KNOWN) {
			attackUnitActionTerran_Marine_Stimmed.setTarget(this.playerUnit.getAttackableEnemyUnitToReactTo());
			retreatActionSteerInRetreatVectorDirectionTerran_Marine_Stimmed
					.setTarget(this.playerUnit.getAttackingEnemyUnitToReactTo());
		}
	}

	@Override
	protected void init() {
		super.init();

		this.abilityActionTerranMarine_StimPack = (AbilityActionTerranMarine_StimPack) this
				.getActionFromInstance(AbilityActionTerranMarine_StimPack.class);

		this.attackUnitActionTerran_Marine_Stimmed = (AttackUnitActionTerran_Marine_Stimmed) this
				.getActionFromInstance(AttackUnitActionTerran_Marine_Stimmed.class);
		this.retreatActionSteerInRetreatVectorDirectionTerran_Marine_Stimmed = (RetreatActionSteerInRetreatVectorDirectionTerran_Marine_Stimmed) this
				.getActionFromInstance(RetreatActionSteerInRetreatVectorDirectionTerran_Marine_Stimmed.class);
	}

}
