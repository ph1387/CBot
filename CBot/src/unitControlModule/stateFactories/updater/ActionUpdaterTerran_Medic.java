package unitControlModule.stateFactories.updater;

import java.util.HashSet;

import bwapi.Unit;
import unitControlModule.stateFactories.actions.executableActions.BaseAction;
import unitControlModule.stateFactories.actions.executableActions.ProtectMoveActionSteerTowardsClosestDamagedUnit;
import unitControlModule.stateFactories.actions.executableActions.RetreatActionSteerInBioUnitDirectionTerran_Medic;
import unitControlModule.stateFactories.actions.executableActions.abilities.AbilityActionTerranMedic_Heal;
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
		HashSet<Unit> possibleUnits = new HashSet<>();
		Unit closestUnit = null;

		if (this.playerUnit.currentState == PlayerUnit.UnitStates.ENEMY_KNOWN) {
			((RetreatActionSteerInBioUnitDirectionTerran_Medic) this.getActionFromInstance(RetreatActionSteerInBioUnitDirectionTerran_Medic.class)).setTarget(this.playerUnit.getClosestEnemyUnitInConfidenceRange());
		}

		// Get all Units that are missing health.
		for (Unit unit : core.Core.getInstance().getPlayer().getUnits()) {
			if (unit != playerUnit.getUnit() && unit.getHitPoints() < unit.getType().maxHitPoints()) {
				boolean isBioUnit = false;

				// Unit is a Bio-Unit.
				switch (unit.getType().toString()) {
				case "Terran_SCV ":
					isBioUnit = true;
					break;
				case "Terran_Marine":
					isBioUnit = true;
					break;
				case "Terran_Firebat":
					isBioUnit = true;
					break;
				case "Terran_Medic":
					isBioUnit = true;
					break;
				case "Terran_Ghost":
					isBioUnit = true;
					break;
				}

				if (isBioUnit) {
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
