package unitControlModule.stateFactories.updater;

import java.util.HashSet;

import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import unitControlModule.stateFactories.actions.executableActions.BaseAction;
import unitControlModule.stateFactories.actions.executableActions.FollowActionTerran_Medic;
import unitControlModule.stateFactories.actions.executableActions.TerranMedic_MoveBackToBase;
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

	// TODO: UML REMOVE
	// private RetreatActionSteerInBioUnitDirectionTerran_Medic
	// retreatActionSteerInBioUnitDirectionTerranMedic;
	// TODO: UML REMOVE
	// private ProtectMoveActionSteerTowardsClosestDamagedUnit
	// protectMoveActionSteerTowardsClosesDamagedUnit;
	private AbilityActionTerranMedic_Heal abilityActionTerranMedicHeal;
	// TODO: UML ADD
	private FollowActionTerran_Medic followActionTerran_Medic;
	// TODO: UML ADD
	private TerranMedic_MoveBackToBase terranMedic_MoveBackToBase;

	public ActionUpdaterTerran_Medic(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	public void update(PlayerUnit playerUnit) {
		Unit closestCenter = this.playerUnit.getClosestCenter();

		// Call super.update() when the Units should react independently to
		// enemy Units and retreat on their own. Init() should not be called
		// then!

		// Get the references to all used actions.
		if (this.initializationMissing) {
			this.init();
			this.initializationMissing = false;
		}

		this.updateHealAndFollowTargets();

		if (closestCenter != null) {
			this.terranMedic_MoveBackToBase.setTarget(closestCenter.getPosition());
		}
	}

	@Override
	protected void init() {
		super.init();

		this.abilityActionTerranMedicHeal = ((AbilityActionTerranMedic_Heal) this
				.getActionFromInstance(AbilityActionTerranMedic_Heal.class));
		this.followActionTerran_Medic = ((FollowActionTerran_Medic) this
				.getActionFromInstance(FollowActionTerran_Medic.class));
		this.terranMedic_MoveBackToBase = ((TerranMedic_MoveBackToBase) this
				.getActionFromInstance(TerranMedic_MoveBackToBase.class));
	}

	// TODO: UML ADD
	/**
	 * Function for updating the target of the
	 * {@link #abilityActionTerranMedicHeal} and
	 * {@link #followActionTerran_Medic} instances.
	 */
	private void updateHealAndFollowTargets() {
		// Either a new Unit has to be assigned as a target or the current
		// target Unit does not miss any hit points and therefore a new one can
		// be assigned.
		if (this.abilityActionTerranMedicHeal.getTarget() == null
				|| (((Unit) this.abilityActionTerranMedicHeal.getTarget())
						.getHitPoints() >= ((Unit) this.abilityActionTerranMedicHeal.getTarget()).getType()
								.maxHitPoints())) {
			Unit closestUnit = this.getClosestHealableDamagedUnit();

			// No healable Unit is found -> Follow the next best Unit!
			if (closestUnit == null) {
				closestUnit = this.getClosestFollowableUnit(this.playerUnit);
			}

			this.abilityActionTerranMedicHeal.setTarget(closestUnit);
			this.followActionTerran_Medic.setTarget(closestUnit);
		}
	}

	// TODO: UML ADD
	/**
	 * Function for finding the closest damaged Unit that can be healed by the
	 * provided PlayerUnit and is not targeted by another Terran_Medic.
	 * 
	 * @return the closest damaged, non targeted Unit that can be healed by the
	 *         provided PlayerUnit.
	 */
	private Unit getClosestHealableDamagedUnit() {
		HashSet<Unit> possibleHealableUnits = this.getHealableUnits(this.playerUnit);
		HashSet<TilePosition> otherMedicTargetedUnitTilePositions = new HashSet<>();
		HashSet<Unit> possibleUnits = new HashSet<>();

		// Gather the TilePositions of each Terran_Medic targets.
		for (Unit unit : this.playerUnit.getInformationStorage().getCurrentGameInformation().getCurrentUnits()
				.getOrDefault(UnitType.Terran_Medic, new HashSet<Unit>())) {
			if (unit != this.playerUnit.getUnit() && unit.getTarget() != null) {
				otherMedicTargetedUnitTilePositions.add(unit.getTarget().getTilePosition());
			}
		}

		// Exclude any Units that are targeted by other Terran_Medics based on
		// the TilePositions.
		for (Unit unit : possibleHealableUnits) {
			if (unit.isCompleted() && unit.getHitPoints() < unit.getType().maxHitPoints()
					&& !otherMedicTargetedUnitTilePositions.contains(unit.getTilePosition())) {
				possibleUnits.add(unit);
			}
		}

		return BaseAction.getClosestUnit(possibleUnits, this.playerUnit.getUnit());
	}

	// TODO: UML ADD
	/**
	 * Function for extracting all references to all current Player-Units on the
	 * map that can be healed by a {@link PlayerUnitTerran_Medic}.
	 * 
	 * @return all current Player-Units on the map that can be healed by a
	 *         {@link PlayerUnitTerran_Medic}.
	 */
	private HashSet<Unit> getHealableUnits(PlayerUnit playerUnit) {
		HashSet<Unit> healableUnits = new HashSet<>();

		// Get all the Units that match the defined UnitTypes except the one
		// that is currently executing the Action.
		for (UnitType unitType : PlayerUnitTerran_Medic.getHealableUnitTypes()) {
			healableUnits.addAll(this.playerUnit.getInformationStorage().getCurrentGameInformation().getCurrentUnits()
					.getOrDefault(unitType, new HashSet<Unit>()));
		}
		healableUnits.remove(this.playerUnit.getUnit());

		return healableUnits;
	}

	// TODO: UML ADD
	/**
	 * Function for finding the closest Unit that the executing Unit can follow.
	 * 
	 * @return the closest Unit that the executing Unit can follow.
	 */
	private Unit getClosestFollowableUnit(PlayerUnit playerUnit) {
		return BaseAction.getClosestUnit(this.getFollowableUnits(), this.playerUnit.getUnit());
	}

	// TODO: UML ADD
	/**
	 * Function for extracting all references to all current Player-Units on the
	 * map that can be followed by a {@link PlayerUnitTerran_Medic}. This
	 * excludes UnitType.Terran_Medics since following them would cause the
	 * executing Unit to clump together with another medic. Therefore these
	 * UnitTypes are excluded to ensure that the Unit is searching for a
	 * Terran_Marine/Firebat or a other healable Unit.
	 * 
	 * @return all current Player-Units on the map that can be followed by a
	 *         {@link PlayerUnitTerran_Medic}.
	 */
	private HashSet<Unit> getFollowableUnits() {
		HashSet<Unit> followableUnits = new HashSet<>();

		// Get all the Units that match the defined UnitTypes except the one
		// that is currently executing the Action.
		for (UnitType unitType : PlayerUnitTerran_Medic.getHealableUnitTypes()) {
			// Do NOT add the Terran_Medic UnitType since this would cause the
			// Units to clump. They would only walk towards each other in pairs.
			if (unitType != UnitType.Terran_Medic) {
				followableUnits.addAll(this.playerUnit.getInformationStorage().getCurrentGameInformation()
						.getCurrentUnits().getOrDefault(unitType, new HashSet<Unit>()));
			}
		}
		followableUnits.remove(this.playerUnit.getUnit());

		return followableUnits;
	}

}
