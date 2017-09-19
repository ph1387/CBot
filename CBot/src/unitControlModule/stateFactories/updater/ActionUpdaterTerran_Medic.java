package unitControlModule.stateFactories.updater;

import java.util.HashSet;

import bwapi.Unit;
import bwapi.UnitType;
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
		Unit closestUnit = this.getClosestHealableDamagedUnit(playerUnit);

		// Get the references to all used actions.
		if (this.initializationMissing) {
			this.init();
			this.initializationMissing = false;
		}

		if (this.playerUnit.currentState == PlayerUnit.UnitStates.ENEMY_KNOWN) {
			this.retreatActionSteerInBioUnitDirectionTerranMedic
					.setTarget(this.playerUnit.getAttackingEnemyUnitToReactTo());
		}

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

	// TODO: UML ADD
	/**
	 * Function for finding the closest damaged Unit that can be healed by the
	 * provided one.
	 * 
	 * @param playerUnit
	 *            the Unit that is going to heal the other Unit.
	 * @return the closest damaged Unit that can be healed by the provided one.
	 */
	private Unit getClosestHealableDamagedUnit(PlayerUnit playerUnit) {
		HashSet<Unit> possibleHealableUnits = new HashSet<>();
		HashSet<Unit> possibleUnits = new HashSet<>();

		// Get all the Units that match the defined UnitTypes except the one
		// that is currently executing the Action.
		for (UnitType unitType : PlayerUnitTerran_Medic.getHealableUnitTypes()) {
			possibleHealableUnits.addAll(playerUnit.getInformationStorage().getCurrentGameInformation()
					.getCurrentUnits().getOrDefault(unitType, new HashSet<Unit>()));
		}
		possibleHealableUnits.remove(playerUnit.getUnit());

		// Extract the ones that are injured.
		for (Unit unit : possibleHealableUnits) {
			if (unit.isCompleted() && unit.getHitPoints() < unit.getType().maxHitPoints()) {
				possibleUnits.add(unit);
			}
		}

		return BaseAction.getClosestUnit(possibleUnits, playerUnit.getUnit());
	}
}
