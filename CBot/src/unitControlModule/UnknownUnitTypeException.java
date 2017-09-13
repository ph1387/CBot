package unitControlModule;

import bwapi.UnitType;

/**
 * UnknownUnitTypeException.java --- Exception for handling unknown UnitTypes.
 * 
 * @author P H - 28.04.2017
 *
 */
public class UnknownUnitTypeException extends Exception {

	// TODO: UML PARAMS
	public UnknownUnitTypeException(UnitType unitType) {
		super("Unknown / Undefined UnitType: " + unitType);
	}
}
