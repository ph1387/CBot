package unitControlModule;

import bwapi.UnitType;

/**
 * UnknownUnitTypeException.java --- Exception for handling unknown UnitTypes.
 * 
 * @author P H - 28.04.2017
 *
 */
public class UnknownUnitTypeException extends Exception {

	public UnknownUnitTypeException(UnitType unitType) {
		super("Unknown / Undefined UnitType: " + unitType);
	}
}
