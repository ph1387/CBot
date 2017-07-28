package buildingOrderModule.simulator;

/**
 * TypeWrapperException.java --- Exception for the {@link TypeWrapper} Class.
 * Used for throwing an Exception when a type is requested (get...) that was not
 * previously stored.
 * 
 * @author P H - 12.07.2017
 *
 */
public class TypeWrapperException extends Exception {

	public TypeWrapperException(String stored, String requested) {
		super("The type '" + requested + "' was requested but '" + stored + "' is stored!");
	}

	// -------------------- Functions

}
