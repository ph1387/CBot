package unitControlModule.unitWrappers;

import java.util.Arrays;
import java.util.List;

import bwapi.Unit;
import bwapi.UnitType;
import informationStorage.InformationStorage;
import unitControlModule.stateFactories.StateFactory;
import unitControlModule.stateFactories.StateFactoryTerran_ScienceVessel;

// TODO: UML ADD
/**
 * PlayerUnitTerran_ScienceVessel.java --- Terran_Science_Vessel Class.
 * 
 * @author P H - 22.09.2017
 *
 */
public class PlayerUnitTerran_ScienceVessel extends PlayerUnitTypeFlying {

	// TODO: UML ADD
	private static final int SUPPORT_PIXEL_DISTANCE = 64;

	// TODO: UML ADD
	// The order in which the different kinds of UnitTypes are supported by the
	// Terran_Science_Vessel.
	private static List<UnitType> SupportableUnitTypes = Arrays.asList(new UnitType[] { UnitType.Terran_Marine,
			UnitType.Terran_Firebat, UnitType.Terran_Goliath, UnitType.Terran_Siege_Tank_Siege_Mode,
			UnitType.Terran_Vulture, UnitType.Terran_Siege_Tank_Tank_Mode });

	public PlayerUnitTerran_ScienceVessel(Unit unit, InformationStorage informationStorage) {
		super(unit, informationStorage);
	}

	// -------------------- Functions

	@Override
	protected StateFactory createFactory() {
		return new StateFactoryTerran_ScienceVessel();
	}

	// ------------------------------ Getter / Setter

	// TODO: UML ADD
	public static int getSupportPixelDistance() {
		return SUPPORT_PIXEL_DISTANCE;
	}

	// TODO: UML ADD
	public static List<UnitType> getSupportableUnitTypes() {
		return SupportableUnitTypes;
	}

}
