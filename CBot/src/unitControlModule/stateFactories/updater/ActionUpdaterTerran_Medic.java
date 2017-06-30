package unitControlModule.stateFactories.updater;

import unitControlModule.stateFactories.actions.executableActions.RetreatActionSteerInBioUnitDirectionTerran_Medic;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * ActionUpdaterTerran_Medic.java --- Updater for updating an
 * {@link AvailableActionsTerran_Medic} instance.
 * 
 * @author P H - 27.06.2017
 *
 */
public class ActionUpdaterTerran_Medic extends ActionUpdaterGeneral {

	public ActionUpdaterTerran_Medic(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	// TODO: UML ADD
	@Override
	public void update(PlayerUnit playerUnit) {
		if(this.playerUnit.currentState == PlayerUnit.UnitStates.ENEMY_KNOWN) {
			((RetreatActionSteerInBioUnitDirectionTerran_Medic) this.getActionFromInstance(RetreatActionSteerInBioUnitDirectionTerran_Medic.class)).setTarget(this.playerUnit.getClosestEnemyUnitInConfidenceRange());
		}
	}
}
