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

	private boolean initializationMissing = true;

	private RetreatActionSteerInBioUnitDirectionTerran_Medic retreatActionSteerInBioUnitDirectionTerranMedic;
	private ProtectMoveActionSteerTowardsClosestDamagedUnit protectMoveActionSteerTowardsClosesDamagedUnit;
	private AbilityActionTerranMedic_Heal abilityActionTerranMedicHeal;

	public ActionUpdaterTerran_Medic(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	public void update(PlayerUnit playerUnit) {
		HashSet<Unit> possibleUnits = new HashSet<>();
		Unit closestUnit = null;

		// Get the references to all used actions.
		if (this.initializationMissing) {
			this.init();
			this.initializationMissing = false;
		}

		if (this.playerUnit.currentState == PlayerUnit.UnitStates.ENEMY_KNOWN) {
			this.retreatActionSteerInBioUnitDirectionTerranMedic
					.setTarget(this.playerUnit.getAttackingEnemyUnitToReactTo());
		}

		// Get all Units that are missing health.
		for (Unit unit : core.Core.getInstance().getPlayer().getUnits()) {
			if (unit != playerUnit.getUnit() && unit.getHitPoints() < unit.getType().maxHitPoints()) {
				// Add the Unit to the possible Units if the Terran_Medic can
				// heal it.
				if (PlayerUnitTerran_Medic.isHealableUnit(unit)) {
					possibleUnits.add(unit);
				}
			}
		}

		// Find the closest one of the Units missing health to heal it.
		closestUnit = BaseAction.getClosestUnit(possibleUnits, playerUnit.getUnit());
		this.protectMoveActionSteerTowardsClosesDamagedUnit.setTarget(closestUnit);
		this.abilityActionTerranMedicHeal.setTarget(closestUnit);
	}

	@Override
	protected void init() {
		super.init();

		this.retreatActionSteerInBioUnitDirectionTerranMedic = ((RetreatActionSteerInBioUnitDirectionTerran_Medic) this
				.getActionFromInstance(RetreatActionSteerInBioUnitDirectionTerran_Medic.class));
		this.protectMoveActionSteerTowardsClosesDamagedUnit = ((ProtectMoveActionSteerTowardsClosestDamagedUnit) this
				.getActionFromInstance(ProtectMoveActionSteerTowardsClosestDamagedUnit.class));
		this.abilityActionTerranMedicHeal = ((AbilityActionTerranMedic_Heal) this
				.getActionFromInstance(AbilityActionTerranMedic_Heal.class));
	}
}
