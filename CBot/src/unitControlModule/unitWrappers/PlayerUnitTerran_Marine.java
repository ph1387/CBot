package unitControlModule.unitWrappers;

import bwapi.Pair;
import bwapi.Unit;
import bwapi.UnitType;
import informationStorage.InformationStorage;
import unitControlModule.stateFactories.StateFactoryTerran_Marine;
import unitControlModule.stateFactories.StateFactory;

//TODO: UML CHANGE SUPERCLASS
/**
 * PlayerUnit_Marine.java --- Terran_Marine Class.
 * 
 * @author P H - 26.02.2017
 *
 */
public class PlayerUnitTerran_Marine extends PlayerUnitTypeRanged {

	// The value that will be added towards the medic multiplier for each medic
	// found.
	private double additionalMedicMultiplierValue = 0.5;

	public PlayerUnitTerran_Marine(Unit unit, InformationStorage informationStorage) {
		super(unit, informationStorage);
	}

	// -------------------- Functions

	@Override
	protected StateFactory createFactory() {
		return new StateFactoryTerran_Marine();
	}

	// TODO: UML CHANGED
	/**
	 * Overridden since Marines use an ability called StimPack, which
	 * effectively reduces their health but significantly improves their
	 * movement. All changes made in the Superclass must be implemented here as
	 * well.
	 * 
	 * @see unitControlModule.unitWrappers.PlayerUnitTypeRanged#generateConfidence()
	 */
	@Override
	protected double generateConfidence() {
		double generatedConfidence = 0.;
		Pair<Double, Double> playerEnemyStrengths = this.generatePlayerAndEnemyStrengths();
		double playerStrengthTotal = playerEnemyStrengths.first;
		double enemyStrengthTotal = playerEnemyStrengths.second;
		int lifeAddtionStimEffect = 0;

		// Custom addition to the default implementation since being stimmed
		// decreases the health but increases the possible damage output.
		if (this.unit.isStimmed()) {
			lifeAddtionStimEffect = 10;
		}

		// TODO: Possible Change: Change the way the life offset is calculated.
		// Calculate the offset of the confidence based on the current Units
		// health.
		double lifeConfidenceMultiplicator = (double) (this.unit.getHitPoints() + lifeAddtionStimEffect)
				/ (double) (this.unit.getType().maxHitPoints());

		// Generate the multiplier based on each medic in the area.
		double medicMultiplier = this.generateMedicMultiplier();

		// Has to be set for following equation
		if (enemyStrengthTotal == 0.) {
			enemyStrengthTotal = 1.;
		}

		// TODO: Possible Change: AirWeapon Implementation
		// Allow kiting if the PlayerUnit is outside of the other Unit's attack
		// range. Also this allows Units to further attack and not running
		// around aimlessly when they are on low health.
		// -> PlayerUnit in range of enemy Unit + extra
		if (this.closestEnemyUnitInConfidenceRange != null
				&& this.closestEnemyUnitInConfidenceRange.getType().groundWeapon().maxRange()
						+ this.extraConfidencePixelRangeToClosestUnits >= this.getUnit()
								.getDistance(this.closestEnemyUnitInConfidenceRange)) {
			generatedConfidence = (playerStrengthTotal / enemyStrengthTotal) * lifeConfidenceMultiplicator
					* medicMultiplier * this.confidenceDefault;
		}
		// -> PlayerUnit out of range of the enemy Unit
		else {
			generatedConfidence = (playerStrengthTotal / enemyStrengthTotal) * this.confidenceDefault;
		}

		return generatedConfidence;
	}

	/**
	 * Function for generating a multiplier based on Terran_Medic Units found in
	 * an area around the Unit.
	 * 
	 * @return a multiplier for each medic in the area around the Unit.
	 */
	private double generateMedicMultiplier() {
		double medicMultiplier = 1.;

		for (Unit unit : this.getAllPlayerUnitsInConfidenceRange()) {
			if (unit.getType() == UnitType.Terran_Medic) {
				medicMultiplier += this.additionalMedicMultiplierValue;
			}
		}

		return medicMultiplier;
	}

}
