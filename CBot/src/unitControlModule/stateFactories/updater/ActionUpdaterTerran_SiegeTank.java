package unitControlModule.stateFactories.updater;

import unitControlModule.stateFactories.actions.executableActions.AttackUnitAction;
import unitControlModule.stateFactories.actions.executableActions.RetreatActionToFurthestUnitInCone;
import unitControlModule.stateFactories.actions.executableActions.RetreatActionToOwnGatheringPoint;
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
		if (this.playerUnit.currentState == PlayerUnit.UnitStates.ENEMY_KNOWN) {
			this.attackMoveToNearestKnownUnitConfiguration();

			((AttackUnitAction) this.getActionFromInstance(AttackUnitAction.class))
					.setTarget(this.playerUnit.getClosestEnemyUnitInConfidenceRange());
			((RetreatActionToFurthestUnitInCone) this.getActionFromInstance(RetreatActionToFurthestUnitInCone.class))
					.setTarget(this.playerUnit.getClosestEnemyUnitInConfidenceRange());
			((RetreatActionToOwnGatheringPoint) this.getActionFromInstance(RetreatActionToOwnGatheringPoint.class))
					.setTarget(this.playerUnit.getClosestEnemyUnitInConfidenceRange());
		}
	}
}
