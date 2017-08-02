package unitControlModule.unitWrappers;

import bwapi.Pair;
import bwapi.Unit;
import informationStorage.InformationStorage;

/**
 * PlayerUnitTypeMelee.java --- Class for melee Units. This class provides basic
 * functionalities that these types of Units require (confidence calculation
 * etc.).
 * 
 * @author P H - 01.08.2017
 *
 */
public abstract class PlayerUnitTypeMelee extends PlayerUnit {

	public PlayerUnitTypeMelee(Unit unit, InformationStorage informationStorage) {
		super(unit, informationStorage);
	}
	
	// -------------------- Functions

	@Override
	protected double generateConfidence() {
		Pair<Double, Double> playerEnemyStrengths = this.generatePlayerAndEnemyStrengths();
		double playerStrengthTotal = playerEnemyStrengths.first;
		double enemyStrengthTotal = playerEnemyStrengths.second;

		// Has to be set for following equation
		if (enemyStrengthTotal == 0.) {
			enemyStrengthTotal = 1.;
		}

		// TODO: Possible Change: AirWeapon Implementation
		return playerStrengthTotal / enemyStrengthTotal;
	}
	
	// ------------------------------ Getter / Setter

}
