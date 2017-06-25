package unitControlModule.stateFactories.updater;

import unitControlModule.stateFactories.actions.AvailableActionsTerran_SiegeTank_SiegeMode;
import unitControlModule.stateFactories.actions.executableActions.AttackUnitActionTerran_SiegeTank_Bombard;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * ActionUpdaterTerran_SiegeTank_SiegeMode.java --- Updater for updating an
 * {@link AvailableActionsTerran_SiegeTank_SiegeMode} instance.
 * 
 * @author P H - 24.06.2017
 *
 */
public class ActionUpdaterTerran_SiegeTank_SiegeMode extends ActionUpdaterGeneral {

	public ActionUpdaterTerran_SiegeTank_SiegeMode(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	public void update(PlayerUnit playerUnit) {
		((AttackUnitActionTerran_SiegeTank_Bombard) this
				.getActionFromInstance(AttackUnitActionTerran_SiegeTank_Bombard.class))
						.setTarget(this.playerUnit.getClosestEnemyUnitInConfidenceRange());
	}
}
