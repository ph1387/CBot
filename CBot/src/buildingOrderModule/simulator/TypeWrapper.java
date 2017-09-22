package buildingOrderModule.simulator;

import java.util.HashMap;

import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;

/**
 * TypeWrapper.java --- Wrapper Class for all types (UnitType, UpgradeType,
 * etc.) for the different Races in the game. This existence of this Class is
 * necessary since the specified types do not have a shared super Class and
 * therefore need to be combined due to the Simulator only working with a single
 * type. The Simulator has to use a single class as wrapper for all its
 * calculations.
 * 
 * @author P H - 12.07.2017
 *
 */
public class TypeWrapper {

	// TODO: Possible Change: Add more than only the Terran values.
	// TODO: Needed Change: Add more types.
	// UnitType wrappers:
	public static final TypeWrapper UnitType_Terran_Barracks = new TypeWrapper(UnitType.Terran_Barracks);
	public static final TypeWrapper UnitType_Terran_Command_Center = new TypeWrapper(UnitType.Terran_Command_Center);
	public static final TypeWrapper UnitType_Terran_Factory = new TypeWrapper(UnitType.Terran_Factory);
	public static final TypeWrapper UnitType_Terran_Firebat = new TypeWrapper(UnitType.Terran_Firebat);
	public static final TypeWrapper UnitType_Terran_Machine_Shop = new TypeWrapper(UnitType.Terran_Machine_Shop);
	public static final TypeWrapper UnitType_Terran_Marine = new TypeWrapper(UnitType.Terran_Marine);
	public static final TypeWrapper UnitType_Terran_Medic = new TypeWrapper(UnitType.Terran_Medic);
	public static final TypeWrapper UnitType_Terran_SCV = new TypeWrapper(UnitType.Terran_SCV);
	public static final TypeWrapper UnitType_Terran_Siege_Tank_Tank_Mode = new TypeWrapper(
			UnitType.Terran_Siege_Tank_Tank_Mode);
	public static final TypeWrapper UnitType_Terran_Vulture = new TypeWrapper(UnitType.Terran_Vulture);
	public static final TypeWrapper UnitType_Terran_Refinery = new TypeWrapper(UnitType.Terran_Refinery);
	public static final TypeWrapper UnitType_Terran_Supply_Depot = new TypeWrapper(UnitType.Terran_Supply_Depot);
	// TODO: UML ADD
	public static final TypeWrapper UnitType_Terran_Academy = new TypeWrapper(UnitType.Terran_Academy);
	// TODO: UML ADD
	public static final TypeWrapper UnitType_Terran_Starport = new TypeWrapper(UnitType.Terran_Starport);
	// TODO: UML ADD
	public static final TypeWrapper UnitType_Terran_Wraith = new TypeWrapper(UnitType.Terran_Wraith);
	// TODO: UML ADD
	public static final TypeWrapper UnitType_Terran_Engineering_Bay = new TypeWrapper(UnitType.Terran_Engineering_Bay);
	// TODO: UML ADD
	public static final TypeWrapper UnitType_Terran_Science_Facility = new TypeWrapper(
			UnitType.Terran_Science_Facility);

	// UpgradeType wrappers:
	// TODO: UML ADD
	public static final TypeWrapper UpgradeType_U_238_Shells = new TypeWrapper(UpgradeType.U_238_Shells);
	// TODO: UML ADD
	public static final TypeWrapper UpgradeType_Terran_Infantry_Armor = new TypeWrapper(
			UpgradeType.Terran_Infantry_Armor);
	// TODO: UML ADD
	public static final TypeWrapper UpgradeType_Terran_Infantry_Weapons = new TypeWrapper(
			UpgradeType.Terran_Infantry_Weapons);

	// TechType wrappers:
	public static final TypeWrapper TechType_Stim_Packs = new TypeWrapper(TechType.Stim_Packs);
	public static final TypeWrapper TechType_Tank_Siege_Mode = new TypeWrapper(TechType.Tank_Siege_Mode);

	// Precomputed HashMaps for an easy conversion from the different types to a
	// TypeWrapper.
	private static final HashMap<UnitType, TypeWrapper> UNITTYPE_TO_TYPEWRAPPER = new HashMap<>();
	private static final HashMap<UpgradeType, TypeWrapper> UPGRADETYPE_TO_TYPEWRAPPER = new HashMap<>();
	private static final HashMap<TechType, TypeWrapper> TECHTYPE_TO_TYPEWRAPPER = new HashMap<>();

	// The actual stored value:
	private UnitType unitType;
	private UpgradeType upgradeType;
	private TechType techType;

	private boolean isUnitType = false;
	private boolean isUpgradeType = false;
	private boolean isTechType = false;

	public TypeWrapper(UnitType unitType) {
		this.unitType = unitType;
		this.isUnitType = true;
	}

	public TypeWrapper(UpgradeType upgradeType) {
		this.upgradeType = upgradeType;
		this.isUpgradeType = true;
	}

	public TypeWrapper(TechType techType) {
		this.techType = techType;
		this.isTechType = true;
	}

	// -------------------- Functions

	/**
	 * Function for initializing all wrapper types. </br>
	 * <b>NOTE:</b></br>
	 * This Function <b>MUST</b> be called at least once if the wrapper is used
	 * for simulations!
	 */
	public static void init() {
		// Insert specific types into the different HashMaps:
		// UnitType wrappers:
		UNITTYPE_TO_TYPEWRAPPER.put(UnitType.Terran_Barracks, UnitType_Terran_Barracks);
		UNITTYPE_TO_TYPEWRAPPER.put(UnitType.Terran_Command_Center, UnitType_Terran_Command_Center);
		UNITTYPE_TO_TYPEWRAPPER.put(UnitType.Terran_Factory, UnitType_Terran_Factory);
		UNITTYPE_TO_TYPEWRAPPER.put(UnitType.Terran_Firebat, UnitType_Terran_Firebat);
		UNITTYPE_TO_TYPEWRAPPER.put(UnitType.Terran_Machine_Shop, UnitType_Terran_Machine_Shop);
		UNITTYPE_TO_TYPEWRAPPER.put(UnitType.Terran_Marine, UnitType_Terran_Marine);
		UNITTYPE_TO_TYPEWRAPPER.put(UnitType.Terran_Medic, UnitType_Terran_Medic);
		UNITTYPE_TO_TYPEWRAPPER.put(UnitType.Terran_SCV, UnitType_Terran_SCV);
		UNITTYPE_TO_TYPEWRAPPER.put(UnitType.Terran_Siege_Tank_Tank_Mode, UnitType_Terran_Siege_Tank_Tank_Mode);
		UNITTYPE_TO_TYPEWRAPPER.put(UnitType.Terran_Vulture, UnitType_Terran_Vulture);
		UNITTYPE_TO_TYPEWRAPPER.put(UnitType.Terran_Refinery, UnitType_Terran_Refinery);
		UNITTYPE_TO_TYPEWRAPPER.put(UnitType.Terran_Supply_Depot, UnitType_Terran_Supply_Depot);
		UNITTYPE_TO_TYPEWRAPPER.put(UnitType.Terran_Academy, UnitType_Terran_Academy);
		UNITTYPE_TO_TYPEWRAPPER.put(UnitType.Terran_Starport, UnitType_Terran_Starport);
		UNITTYPE_TO_TYPEWRAPPER.put(UnitType.Terran_Wraith, UnitType_Terran_Wraith);
		UNITTYPE_TO_TYPEWRAPPER.put(UnitType.Terran_Engineering_Bay, UnitType_Terran_Engineering_Bay);
		UNITTYPE_TO_TYPEWRAPPER.put(UnitType.Terran_Science_Facility, UnitType_Terran_Science_Facility);

		// UpgradeType wrappers:
		UPGRADETYPE_TO_TYPEWRAPPER.put(UpgradeType.U_238_Shells, UpgradeType_U_238_Shells);
		UPGRADETYPE_TO_TYPEWRAPPER.put(UpgradeType.Terran_Infantry_Armor, UpgradeType_Terran_Infantry_Armor);
		UPGRADETYPE_TO_TYPEWRAPPER.put(UpgradeType.Terran_Infantry_Weapons, UpgradeType_Terran_Infantry_Weapons);

		// TechType wrappers:
		TECHTYPE_TO_TYPEWRAPPER.put(TechType.Stim_Packs, TechType_Stim_Packs);
		TECHTYPE_TO_TYPEWRAPPER.put(TechType.Tank_Siege_Mode, TechType_Tank_Siege_Mode);
	}

	/**
	 * Function for transforming a UnitType into the corresponding TypeWrapper.
	 * 
	 * @param unitType
	 *            the UnitType that will be converted.
	 * @return the corresponding TypeWrapper for the provided UnitType.
	 */
	public static TypeWrapper generateFrom(UnitType unitType) {
		return UNITTYPE_TO_TYPEWRAPPER.get(unitType);
	}

	/**
	 * Function for transforming a UpgradeType into the corresponding
	 * TypeWrapper.
	 * 
	 * @param upgradeType
	 *            the UpgradeType that will be converted.
	 * @return the corresponding TypeWrapper for the provided UpgradeType.
	 */
	public static TypeWrapper generateFrom(UpgradeType upgradeType) {
		return UPGRADETYPE_TO_TYPEWRAPPER.get(upgradeType);
	}

	/**
	 * Function for transforming a TechType into the corresponding TypeWrapper.
	 * 
	 * @param techType
	 *            the UpgradeType that will be converted.
	 * @return the corresponding TypeWrapper for the provided TechType.
	 */
	public static TypeWrapper generateFrom(TechType techType) {
		return TECHTYPE_TO_TYPEWRAPPER.get(techType);
	}

	@Override
	public String toString() {
		// Find the type that is actually stored in the TypeWrapper.
		if (this.isUnitType) {
			return this.unitType.toString();
		} else if (this.isUpgradeType) {
			return this.upgradeType.toString();
		} else if (this.isTechType) {
			return this.techType.toString();
		} else {
			return "---NOTHING---";
		}
	}

	/**
	 * Function for generating a TypeWrapperException.
	 * 
	 * @param requested
	 *            the requested type in form of a String.
	 * @return a TypeWrapperException with the proper information stored.
	 */
	private TypeWrapperException generateTypeWrapperException(String requested) {
		String stored;

		// Find the type that is actually stored in the TypeWrapper.
		if (this.isUnitType) {
			stored = "UnitType";
		} else if (this.isUpgradeType) {
			stored = "UpgradeType";
		} else if (this.isTechType) {
			stored = "TechType";
		} else {
			stored = "---NOTHING---";
		}

		return new TypeWrapperException(stored, requested);
	}

	// ------------------------------ Getter / Setter

	public UnitType getUnitType() {
		try {
			if (this.isUnitType) {
				return this.unitType;
			} else {
				throw this.generateTypeWrapperException("UnitType");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public UpgradeType getUpgradeType() {
		try {
			if (this.isUpgradeType) {
				return this.upgradeType;
			} else {
				throw this.generateTypeWrapperException("UpgradeType");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public TechType getTechType() {
		try {
			if (this.isTechType) {
				return this.techType;
			} else {
				throw this.generateTypeWrapperException("TechType");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean isUnitType() {
		return isUnitType;
	}

	public boolean isUpgradeType() {
		return isUpgradeType;
	}

	public boolean isTechType() {
		return isTechType;
	}
}
