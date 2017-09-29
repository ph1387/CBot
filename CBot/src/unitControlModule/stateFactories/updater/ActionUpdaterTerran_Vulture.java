package unitControlModule.stateFactories.updater;

import unitControlModule.stateFactories.actions.AvailableActionsTerran_Vulture;
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

	// TODO: UML ADD
	private AbilityActionTerranVuture_SpiderMines abilityActionTerranVuture_SpiderMines;
	// TODO: UML ADD
	private TerranVulture_SpiderMines_RepositionEnemy terranVulture_SpiderMines_RepositionEnemy;

	public ActionUpdaterTerran_Vulture(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	// TODO: UML ADD
	@Override
	public void update(PlayerUnit playerUnit) {
		super.update(playerUnit);

		// Always place the mines below the Unit itself.
		this.abilityActionTerranVuture_SpiderMines.setTarget(this.playerUnit.getUnit().getPosition());
		this.terranVulture_SpiderMines_RepositionEnemy.setTarget(this.playerUnit.getAttackableEnemyUnitToReactTo());
	}

	// TODO: UML ADD
	@Override
	protected void init() {
		super.init();

		this.abilityActionTerranVuture_SpiderMines = (AbilityActionTerranVuture_SpiderMines) this
				.getActionFromInstance(AbilityActionTerranVuture_SpiderMines.class);
		this.terranVulture_SpiderMines_RepositionEnemy = (TerranVulture_SpiderMines_RepositionEnemy) this
				.getActionFromInstance(TerranVulture_SpiderMines_RepositionEnemy.class);
	}
}
