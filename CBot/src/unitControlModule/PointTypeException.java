package unitControlModule;

import unitControlModule.Point.Type;

/**
 * PointTypeException.java --- Exception for displaying a Point type error.
 * 
 * @author P H - 26.04.2017
 *
 */
public class PointTypeException extends Exception {

	public PointTypeException(Type type) {
		super("The Type of the Point used has to be " + type.toString() + "!");
	}

}
