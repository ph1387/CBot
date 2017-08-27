package unitControlModule.unitWrappers;

import bwapi.Pair;
import bwapi.Unit;
import informationStorage.InformationStorage;

/**
 * PlayerUnitTypeRanged.java --- Class for ranged Units. This class provides
 * basic functionalities that these types of Units require (confidence
 * calculation etc.).
 * 
 * @author P H - 01.08.2017
 *
 */
public abstract class PlayerUnitTypeRanged extends PlayerUnit {

	public PlayerUnitTypeRanged(Unit unit, InformationStorage informationStorage) {
		super(unit, informationStorage);
	}

	// -------------------- Functions

	@Override
	protected double generateConfidence() {
		double generatedConfidence = 0.;
		Pair<Double, Double> playerEnemyStrengths = this.generatePlayerAndEnemyStrengths();
		double playerStrengthTotal = playerEnemyStrengths.first;
		double enemyStrengthTotal = playerEnemyStrengths.second;
		// TODO: Possible Change: Change the way the life offset is calculated.
		// Calculate the offset of the confidence based on the current Units
		// health.
		double lifeConfidenceMultiplicator = (double) (this.unit.getHitPoints())
				/ (double) (this.unit.getType().maxHitPoints());

		// Calculate the confidence based on the strength difference:
		// No enemy = Maximum confidence.
		if (enemyStrengthTotal == 0.) {
			generatedConfidence = 1.;
		} else {
			// TODO: Possible Change: AirWeapon Implementation
			// Allow kiting if the PlayerUnit is outside of the other Unit's
			// attack range. Also this allows Units to further attack and not
			// running around aimlessly when they are on low health.
			// -> PlayerUnit in range of enemy Unit + extra
			if (this.closestEnemyUnitInConfidenceRange != null
					&& this.closestEnemyUnitInConfidenceRange.getType().groundWeapon().maxRange()
							+ this.extraConfidencePixelRangeToClosestUnits >= this.getUnit()
									.getDistance(this.closestEnemyUnitInConfidenceRange)) {
				generatedConfidence = (playerStrengthTotal / enemyStrengthTotal) * lifeConfidenceMultiplicator
						* this.confidenceDefault;
			}
			// -> PlayerUnit out of range of the enemy Unit
			else {
				generatedConfidence = (playerStrengthTotal / enemyStrengthTotal) * this.confidenceDefault;
			}
		}

		return generatedConfidence;
	}

	// ------------------------------ Getter / Setter

}
