package unitControlModule.unitWrappers;

import bwapi.Unit;
import informationStorage.InformationStorage;
import unitControlModule.stateFactories.StateFactory;
import unitControlModule.stateFactories.StateFactoryTerran_Medic;

//TODO: UML CHANGE SUPERCLASS
/**
 * PlayerUnitTerran_Medic.java --- Terran_Medic Class. Used for healing
 * Bio-Units on the map like Terran_Marines and Terran_Firebats.
 * 
 * @author P H - 27.06.2017
 *
 */
public class PlayerUnitTerran_Medic extends PlayerUnitTypeRanged {

	public PlayerUnitTerran_Medic(Unit unit, InformationStorage informationStorage) {
		super(unit, informationStorage);
	}

	// -------------------- Functions

	@Override
	protected StateFactory createFactory() {
		return new StateFactoryTerran_Medic();
	}

	/**
	 * Function for testing if a specific Unit is a supportable (Bio-) Unit that
	 * the Medic can heal.
	 * 
	 * @param unit
	 *            the Unit that will be tested / is targeted.
	 * @return true or false depending if the Terran_Medic is able to heal the
	 *         provided Unit.
	 */
	public static boolean isHealableUnit(Unit unit) {
		boolean isBioUnit = false;

		// Unit is a Bio-Unit.
		switch (unit.getType().toString()) {
		case "Terran_SCV ":
			isBioUnit = true;
			break;
		case "Terran_Marine":
			isBioUnit = true;
			break;
		case "Terran_Firebat":
			isBioUnit = true;
			break;
		case "Terran_Medic":
			isBioUnit = true;
			break;
		case "Terran_Ghost":
			isBioUnit = true;
			break;
		}

		return isBioUnit;
	}
}
