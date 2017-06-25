package unitControlModule.stateFactories.updater;

import unitControlModule.stateFactories.actions.AvailableActionsTerran_SiegeTank;
import unitControlModule.stateFactories.actions.executableActions.AttackUnitActionTerran_SiegeTank_Bombard;
import unitControlModule.unitWrappers.PlayerUnit;


/**
 * Terran_SiegeTankActionUpdater.java --- Updater for updating an
 * {@link AvailableActionsTerran_SiegeTank} instance.
 * 
 * @author P H - 25.03.2017
 *
 */
public class ActionUpdaterTerran_SiegeTank extends ActionUpdaterDefault {

	public ActionUpdaterTerran_SiegeTank(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	public void update(PlayerUnit playerUnit) {
		super.update(playerUnit);
		
		((AttackUnitActionTerran_SiegeTank_Bombard) this.getActionFromInstance(AttackUnitActionTerran_SiegeTank_Bombard.class)).setTarget(this.playerUnit.getClosestEnemyUnitInConfidenceRange());
	}
}
