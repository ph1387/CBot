package unitControlModule.unitWrappers;

import java.util.Arrays;
import java.util.List;

import bwapi.Unit;
import bwapi.UnitType;
import informationStorage.InformationStorage;
import unitControlModule.stateFactories.StateFactory;
import unitControlModule.stateFactories.StateFactoryTerran_Medic;

/**
 * PlayerUnitTerran_Medic.java --- Terran_Medic Class. Used for healing
 * Bio-Units on the map like Terran_Marines and Terran_Firebats.
 * 
 * @author P H - 27.06.2017
 *
 */
public class PlayerUnitTerran_Medic extends PlayerUnitTypeRanged {

	private static final int HEAL_PIXEL_DISTANCE = 96;
	private static List<UnitType> HealableUnitTypes = Arrays
			.asList(new UnitType[] { UnitType.Terran_Firebat, UnitType.Terran_Marine, UnitType.Terran_Medic });

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
		return HealableUnitTypes.contains(unit.getType());
	}

	// ------------------------------ Getter / Setter

	public static int getHealPixelDistance() {
		return HEAL_PIXEL_DISTANCE;
	}

	public static List<UnitType> getHealableUnitTypes() {
		return HealableUnitTypes;
	}
}
