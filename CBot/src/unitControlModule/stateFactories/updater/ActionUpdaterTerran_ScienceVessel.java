package unitControlModule.stateFactories.updater;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import bwapi.Pair;
import bwapi.Unit;
import bwapi.UnitType;
import unitControlModule.stateFactories.actions.AvailableActionsTerran_ScienceVessel;
import unitControlModule.stateFactories.actions.executableActions.FollowActionTerran_ScienceVessel;
import unitControlModule.stateFactories.actions.executableActions.abilities.AbilityActionTerranScienceVessel_DefensiveMatrix;
import unitControlModule.unitWrappers.PlayerUnit;
import unitControlModule.unitWrappers.PlayerUnitTerran_ScienceVessel;

// TODO: UML ADD
/**
 * ActionUpdaterTerran_ScienceVessel.java --- Updater for updating an
 * {@link AvailableActionsTerran_ScienceVessel} instance.
 * 
 * @author P H - 23.09.2017
 *
 */
public class ActionUpdaterTerran_ScienceVessel extends ActionUpdaterGeneral {

	private boolean initializationMissing = true;

	private FollowActionTerran_ScienceVessel followActionTerran_ScienceVessel;
	private AbilityActionTerranScienceVessel_DefensiveMatrix abilityActionTerranScienceVessel_DefensiveMatrix;

	// The additional life granted by the Defensive_Maxtrix ability.
	private double additionalLife = 250.;
	// A general health multiplier used in the simple simulation to account for
	// the different multipliers used by the UnitTrackerModule.
	// Note:
	// The higher the value, the sooner the Science_Vessel will use the ability!
	private int generalHealthMultiplier = 200;

	// The percentage of health at which the Science_Vessel considers the target
	// Unit "at low health".
	private double lowHealthThreshold = 0.75;

	public ActionUpdaterTerran_ScienceVessel(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	// TODO: UML ADD
	@Override
	public void update(PlayerUnit playerUnit) {
		// Call super.update() when the Units should react independently to
		// enemy Units and retreat on their own. Init() should not be called
		// then!
		// super.update(playerUnit);

		// Get the references to all used actions.
		if (this.initializationMissing) {
			this.init();
			this.initializationMissing = false;
		}

		this.followActionTerran_ScienceVessel.setTarget(this.getClosestSupportableUnit(playerUnit));

		// Find a Unit near the executing Science_Vessel that requires help in
		// form of a life boost.
		this.abilityActionTerranScienceVessel_DefensiveMatrix.setTarget(this.getDefensiveMatrixTarget(playerUnit));
	}

	// TODO: UML ADD
	@Override
	protected void init() {
		super.init();

		this.followActionTerran_ScienceVessel = ((FollowActionTerran_ScienceVessel) this
				.getActionFromInstance(FollowActionTerran_ScienceVessel.class));
		this.abilityActionTerranScienceVessel_DefensiveMatrix = ((AbilityActionTerranScienceVessel_DefensiveMatrix) this
				.getActionFromInstance(AbilityActionTerranScienceVessel_DefensiveMatrix.class));
	}

	// TODO: UML ADD
	/**
	 * Function for finding the closest Unit whose UnitType matches one of the
	 * listed ones in the {@link PlayerUnitTerran_ScienceVessel}'s List of
	 * supportable UnitTypes. The List defined in this Class determines the
	 * order in which the different UnitTypes are searched through. The
	 * UnitTypes at the beginning of the List are preferred while the ones at
	 * the end are considered last.
	 * 
	 * @param playerUnit
	 *            the Unit that is executing the Action.
	 * @return the closest Unit that the executing Unit can support.
	 */
	private Unit getClosestSupportableUnit(PlayerUnit playerUnit) {
		Unit closestSupportableUnit = (Unit) this.followActionTerran_ScienceVessel.getTarget();
		double closestSupportableUnitDistance = 0.;

		// Prevent access violation errors.
		if(closestSupportableUnit != null) {
			closestSupportableUnitDistance = playerUnit.getUnit().getDistance(closestSupportableUnit);
		}
		
		
		// Find the closest Unit whose UnitType matches one of the
		// ones supported by the Terran_Science_Vessel.
		for (UnitType unitType : PlayerUnitTerran_ScienceVessel.getSupportableUnitTypes()) {
			for (Unit unit : playerUnit.getInformationStorage().getCurrentGameInformation().getCurrentUnits()
					.getOrDefault(unitType, new HashSet<Unit>())) {
				double currentDistance = playerUnit.getUnit().getDistance(unit);

				if (!this.playerUnit.getInformationStorage().getScienceVesselStorage().isBeingFollowed(unit)) {
					if (closestSupportableUnit == null || currentDistance < closestSupportableUnitDistance) {
						closestSupportableUnit = unit;
						closestSupportableUnitDistance = currentDistance;
					}
				}
			}

			// The order in which the UnitTypes are listed in the ScienceVessel
			// does matter!
			if (closestSupportableUnit != null) {
				break;
			}
		}

		return closestSupportableUnit;
	}

	/**
	 * Function for finding the closest Unit of the ones being currently around
	 * the {@link PlayerUnitTerran_ScienceVessel} that is a viable target for
	 * the Defensive_Matrix ability of the vessel.
	 * 
	 * @param playerUnit
	 *            the Unit that is executing the Action.
	 * @return the closest Unit that the executing Unit can / should use the
	 *         ability on.
	 */
	private Unit getDefensiveMatrixTarget(final PlayerUnit playerUnit) {
		List<Unit> playerUnits = new ArrayList<>(playerUnit.getAllPlayerUnitsInConfidenceRange());
		Unit possibleTarget = null;

		// Sort the List based on the distance towards the PlayerUnit.
		playerUnits.sort(new Comparator<Unit>() {

			@Override
			public int compare(Unit u1, Unit u2) {
				return Integer.compare(playerUnit.getUnit().getDistance(u1), playerUnit.getUnit().getDistance(u2));
			}
		});

		// Iterate through the sorted List. This way the closest one of the
		// Units matching the criteria is picked.
		for (Unit unit : playerUnits) {
			if (this.doesConfidenceSwitch(playerUnit) || this.isInDanger(unit)) {
				possibleTarget = unit;

				break;
			}
		}
		return possibleTarget;
	}

	/**
	 * Function for testing if a shield (= Life boost) would cause the
	 * confidence of the target Unit to exceed the set threshold for it.
	 * 
	 * @param playerUnit
	 *            the executing Science_Vessel.
	 * @return true if the Unit would be confident with additional health, false
	 *         if it is already is or the boost is not enough.
	 */
	private boolean doesConfidenceSwitch(PlayerUnit playerUnit) {
		boolean shieldingNeeded = false;

		// Get the different strengths of the Player and the enemy.
		Pair<Double, Double> healthStrengths = playerUnit.generatePlayerAndEnemyHealthStrengths();
		Pair<Double, Double> groundStrengths = playerUnit.generatePlayerAndEnemyGroundStrengths();

		// Do a simple simulation regarding the confidence of the affected
		// target Unit. If a health boost causes the confidence to exceed the
		// set threshold apply it to the target.
		double simpleConfidence = (healthStrengths.first + groundStrengths.first)
				/ Math.max(healthStrengths.second + groundStrengths.second, 1.);
		double simpleSimulatedConfidence = (healthStrengths.first + (this.additionalLife * this.generalHealthMultiplier)
				+ groundStrengths.first) / Math.max(healthStrengths.second + groundStrengths.second, 1.);

		if (PlayerUnit.isConfidenceBelowThreshold(simpleConfidence)
				&& PlayerUnit.isConfidenceAboveThreshold(simpleSimulatedConfidence)) {
			shieldingNeeded = true;
		}
		return shieldingNeeded;
	}

	/**
	 * Function for testing if a Unit is in danger. This includes being attacked
	 * by an enemy Unit and having less hit points than a set percentage of the
	 * max ones left.
	 * 
	 * @param unit
	 *            the Unit that is going to be tested.
	 * @return true if the Unit is in danger, false if not.
	 */
	private boolean isInDanger(Unit unit) {
		boolean isBeingAttacked = unit.isUnderAttack();
		boolean hasLowHealth = (double) (unit.getHitPoints()) <= this.lowHealthThreshold
				* (double) (unit.getType().maxHitPoints());

		return isBeingAttacked && hasLowHealth;
	}

}
