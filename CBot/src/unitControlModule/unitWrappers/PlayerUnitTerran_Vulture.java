package unitControlModule.unitWrappers;

import bwapi.Unit;
import informationStorage.InformationStorage;
import unitControlModule.stateFactories.StateFactoryTerran_Vulture;
import unitControlModule.stateFactories.StateFactory;

//TODO: UML CHANGE SUPERCLASS
/**
 * PlayerUnit_Vulture.java --- Terran Vulture Class.
 * 
 * @author P H - 23.03.2017
 *
 */
public class PlayerUnitTerran_Vulture extends PlayerUnitTypeRanged {

	public PlayerUnitTerran_Vulture(Unit unit, InformationStorage informationStorage) {
		super(unit, informationStorage);

		// TODO: Possible Change: Update confidenceDefaultRange based on the
		// closestEnemy's weapon range
		this.extraConfidencePixelRangeToClosestUnits = 112;
		this.confidenceDefault = 0.35;
	}

	// -------------------- Functions

	@Override
	protected StateFactory createFactory() {
		return new StateFactoryTerran_Vulture();
	}

}
