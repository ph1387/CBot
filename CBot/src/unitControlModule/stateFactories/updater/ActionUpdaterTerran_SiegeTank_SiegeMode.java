package unitControlModule.stateFactories.updater;

import unitControlModule.stateFactories.actions.AvailableActionsTerran_SiegeTank_SiegeMode;
import unitControlModule.stateFactories.actions.executableActions.AttackUnitActionTerran_SiegeTank_Bombard;
import unitControlModule.stateFactories.actions.executableActions.abilities.AbilityActionTerranSiegeTank_TankMode;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * ActionUpdaterTerran_SiegeTank_SiegeMode.java --- Updater for updating an
 * {@link AvailableActionsTerran_SiegeTank_SiegeMode} instance.
 * 
 * @author P H - 24.06.2017
 *
 */
public class ActionUpdaterTerran_SiegeTank_SiegeMode extends ActionUpdaterGeneral {

	private AbilityActionTerranSiegeTank_TankMode abilityActionTerranSiegeTankTankMode;
	private AttackUnitActionTerran_SiegeTank_Bombard attackUnitActionTerranSiegeTankBombard;

	public ActionUpdaterTerran_SiegeTank_SiegeMode(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	public void update(PlayerUnit playerUnit) {
		// TODO: Possible Change: Only perform once.
		this.abilityActionTerranSiegeTankTankMode.setTarget(this.playerUnit);

		this.attackUnitActionTerranSiegeTankBombard.setTarget(this.playerUnit.getClosestEnemyUnitInConfidenceRange());
	}

	@Override
	protected void init() {
		super.init();

		this.abilityActionTerranSiegeTankTankMode = ((AbilityActionTerranSiegeTank_TankMode) this
				.getActionFromInstance(AbilityActionTerranSiegeTank_TankMode.class));
		this.attackUnitActionTerranSiegeTankBombard = ((AttackUnitActionTerran_SiegeTank_Bombard) this
				.getActionFromInstance(AttackUnitActionTerran_SiegeTank_Bombard.class));
	}

}
