package unitControlModule.unitWrappers;

import bwapi.Pair;
import bwapi.Unit;
import informationStorage.InformationStorage;
import unitControlModule.stateFactories.StateFactoryTerran_Marine;
import unitControlModule.stateFactories.StateFactory;

/**
 * PlayerUnit_Marine.java --- Terran_Marine Class.
 * 
 * @author P H - 26.02.2017
 *
 */
public class PlayerUnitTerran_Marine extends PlayerUnit {

	public PlayerUnitTerran_Marine(Unit unit, InformationStorage informationStorage) {
		super(unit, informationStorage);
	}

	// -------------------- Functions

	@Override
	protected StateFactory createFactory() {
		return new StateFactoryTerran_Marine();
	}

	/**
	 * Overridden since Marines use an ability called StimPack, which
	 * effectively reduces their health but significantly improves their
	 * movement. All changes made in the Superclass must be implemented here as
	 * well.
	 * 
	 * @see unitControlModule.unitWrappers.PlayerUnit#updateConfidence()
	 */
	@Override
	protected void updateConfidence() {
		Pair<Double, Double> playerEnemyStrengths = this.generatePlayerAndEnemyStrengths();
		double playerStrengthTotal = playerEnemyStrengths.first;
		double enemyStrengthTotal = playerEnemyStrengths.second;
		int lifeAddtionStimEffect = 0;

		if (this.unit.isStimmed()) {
			lifeAddtionStimEffect = 10;
		}

		// TODO: Possible Change: Change the way the life offset is calculated.
		// Calculate the offset of the confidence based on the current Units
		// health.
		double lifeConfidenceMultiplicator = (double) (this.unit.getHitPoints() + lifeAddtionStimEffect)
				/ (double) (this.unit.getType().maxHitPoints());

		// Has to be set for following equation
		if (enemyStrengthTotal == 0.) {
			enemyStrengthTotal = 1.;
		}

		// TODO: Possible Change: AirWeapon Implementation
		// Allow kiting if the PlayerUnit is outside of the other Unit's attack
		// range. Also this allows Units to further attack and not running
		// around aimlessly when they are on low health.
		// -> PlayerUnit in range of enemy Unit + extra
		if (this.closestEnemyUnitInConfidenceRange.getType().groundWeapon().maxRange()
				+ this.extraConfidencePixelRangeToClosestUnits >= this.getUnit()
						.getDistance(this.closestEnemyUnitInConfidenceRange)) {
			this.confidence = (playerStrengthTotal / enemyStrengthTotal) * lifeConfidenceMultiplicator
					* this.confidenceDefault;
		}
		// -> PlayerUnit out of range of the enemy Unit
		else {
			this.confidence = (playerStrengthTotal / enemyStrengthTotal);
		}
	}

}
