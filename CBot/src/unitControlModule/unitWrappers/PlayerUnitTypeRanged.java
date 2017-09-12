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
