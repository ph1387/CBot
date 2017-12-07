package unitControlModule.unitWrappers;

import java.util.HashSet;

import bwapi.Pair;
import bwapi.Unit;
import core.Core;
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

	// The additional TileRange to the Unit's weapon range that enemy Units are
	// still counted towards the ones being inside the executing Unit's range.
	// TODO: UML ADD
	private int additionalTileRange = 2;
	// TODO: UML ADD
	private int additionalRange = Core.getInstance().getTileSize() * this.additionalTileRange;

	public PlayerUnitTypeMelee(Unit unit, InformationStorage informationStorage) {
		super(unit, informationStorage);
	}

	// -------------------- Functions

	@Override
	protected double generateConfidence() {
		double generatedConfidence = 0.;
		Pair<Double, Double> playerEnemyGroundStrengths = this.generatePlayerAndEnemyGroundStrengths();
		Pair<Double, Double> playerEnemyHealthStrengths = this.generatePlayerAndEnemyHealthStrengths();
		double playerStrengthTotal = playerEnemyGroundStrengths.first + playerEnemyHealthStrengths.first;
		double enemyStrengthTotal = playerEnemyGroundStrengths.second + playerEnemyHealthStrengths.second;

		// Calculate the confidence based on the strength difference:
		// No enemy = Maximum confidence.
		if (enemyStrengthTotal == 0.) {
			generatedConfidence = 1.;
		} else {
			generatedConfidence = playerStrengthTotal / enemyStrengthTotal;
		}

		return generatedConfidence;
	}

	// TODO: UML ADD
	@Override
	public HashSet<Unit> getAllEnemyUnitsInWeaponRange() {
		return this.getAllEnemyUnitsInRange(this.unit.getType().groundWeapon().maxRange() + this.additionalRange);
	}

	// ------------------------------ Getter / Setter

}
