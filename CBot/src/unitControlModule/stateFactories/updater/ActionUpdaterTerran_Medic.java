package unitControlModule.stateFactories.updater;

import java.util.HashSet;

import bwapi.Unit;
import unitControlModule.stateFactories.actions.executableActions.BaseAction;
import unitControlModule.stateFactories.actions.executableActions.ProtectMoveActionSteerTowardsClosestDamagedUnit;
import unitControlModule.stateFactories.actions.executableActions.RetreatActionSteerInBioUnitDirectionTerran_Medic;
import unitControlModule.stateFactories.actions.executableActions.abilities.AbilityActionTerranMedic_Heal;
import unitControlModule.unitWrappers.PlayerUnit;
import unitControlModule.unitWrappers.PlayerUnitTerran_Medic;

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
		HashSet<Unit> possibleUnits = new HashSet<>();
		Unit closestUnit = null;

		if (this.playerUnit.currentState == PlayerUnit.UnitStates.ENEMY_KNOWN) {
			((RetreatActionSteerInBioUnitDirectionTerran_Medic) this.getActionFromInstance(RetreatActionSteerInBioUnitDirectionTerran_Medic.class)).setTarget(this.playerUnit.getClosestEnemyUnitInConfidenceRange());
		}

		// Get all Units that are missing health.
		for (Unit unit : core.Core.getInstance().getPlayer().getUnits()) {
			if (unit != playerUnit.getUnit() && unit.getHitPoints() < unit.getType().maxHitPoints()) {
				// Add the Unit to the possible Units if the Terran_Medic can heal it.
				if (((PlayerUnitTerran_Medic) playerUnit).isHealableUnit(unit)) {
					possibleUnits.add(unit);
				}
			}
		}

		// Find the closest one of the Units missing health to heal it.
		closestUnit = BaseAction.getClosestUnit(possibleUnits, playerUnit.getUnit());
		((ProtectMoveActionSteerTowardsClosestDamagedUnit) this.getActionFromInstance(ProtectMoveActionSteerTowardsClosestDamagedUnit.class)).setTarget(closestUnit);
		((AbilityActionTerranMedic_Heal) this.getActionFromInstance(AbilityActionTerranMedic_Heal.class)).setTarget(closestUnit);
	}
}
