package unitControlModule.unitWrappers;

import bwapi.Pair;
import bwapi.Unit;
import informationStorage.InformationStorage;

/**
 * PlayerUnitTypeFlying.java --- Class for flying Units. This class provides
 * basic functionalities that these types of Units require (confidence
 * calculation etc.).
 * 
 * @author P H - 13.09.2017
 *
 */
public abstract class PlayerUnitTypeFlying extends PlayerUnit {

	public PlayerUnitTypeFlying(Unit unit, InformationStorage informationStorage) {
		super(unit, informationStorage);
	}

	// -------------------- Functions

	@Override
	protected double generateConfidence() {
		double generatedConfidence = 0.;
		Pair<Double, Double> playerEnemyAirStrengths = this.generatePlayerAndEnemyAirStrengths();
		Pair<Double, Double> playerEnemyHealthStrengths = this.generatePlayerAndEnemyHealthStrengths();
		double playerStrengthTotal = playerEnemyAirStrengths.first + playerEnemyHealthStrengths.first;
		double enemyStrengthTotal = playerEnemyAirStrengths.second + playerEnemyHealthStrengths.second;

		// Calculate the confidence based on the strength difference:
		// No enemy = Maximum confidence.
		if (enemyStrengthTotal == 0.) {
			generatedConfidence = 1.;
		} else {
			generatedConfidence = playerStrengthTotal / enemyStrengthTotal;
		}

		return generatedConfidence;
	}

	// ------------------------------ Getter / Setter

}
