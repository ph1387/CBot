package unitControlModule.stateFactories.updater;

import unitControlModule.stateFactories.actions.AvailableActionsTerran_Vulture;
import unitControlModule.stateFactories.actions.executableActions.RetreatActionSteerInRetreatVectorDirectionTerran_VultureMicro;
import unitControlModule.stateFactories.actions.executableActions.TerranVulture_SpiderMines_RepositionEnemy;
import unitControlModule.stateFactories.actions.executableActions.abilities.AbilityActionTerranVuture_SpiderMines;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * ActionUpdaterTerran_Vulture.java --- Updater for updating an
 * {@link AvailableActionsTerran_Vulture} instance.
 * 
 * @author P H - 04.07.2017
 *
 */
public class ActionUpdaterTerran_Vulture extends ActionUpdaterDefault {

	private AbilityActionTerranVuture_SpiderMines abilityActionTerranVuture_SpiderMines;
	private TerranVulture_SpiderMines_RepositionEnemy terranVulture_SpiderMines_RepositionEnemy;
	private RetreatActionSteerInRetreatVectorDirectionTerran_VultureMicro retreatActionSteerInRetreatVectorDirectionTerran_VultureMicro;

	public ActionUpdaterTerran_Vulture(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	public void update(PlayerUnit playerUnit) {
		super.update(playerUnit);

		// Enable a secondary retreat action: The micro one!
		this.retreatActionSteerInRetreatVectorDirectionTerran_VultureMicro
				.setTarget(this.playerUnit.getAttackableEnemyUnitToReactTo());

		// Always place the mines below the Unit itself.
		this.abilityActionTerranVuture_SpiderMines.setTarget(this.playerUnit.getUnit().getPosition());
		this.terranVulture_SpiderMines_RepositionEnemy.setTarget(this.playerUnit.getAttackableEnemyUnitToReactTo());
	}

	@Override
	protected void init() {
		super.init();

		this.abilityActionTerranVuture_SpiderMines = (AbilityActionTerranVuture_SpiderMines) this
				.getActionFromInstance(AbilityActionTerranVuture_SpiderMines.class);
		this.terranVulture_SpiderMines_RepositionEnemy = (TerranVulture_SpiderMines_RepositionEnemy) this
				.getActionFromInstance(TerranVulture_SpiderMines_RepositionEnemy.class);
		this.retreatActionSteerInRetreatVectorDirectionTerran_VultureMicro = (RetreatActionSteerInRetreatVectorDirectionTerran_VultureMicro) this
				.getActionFromInstance(RetreatActionSteerInRetreatVectorDirectionTerran_VultureMicro.class);
	}
}
