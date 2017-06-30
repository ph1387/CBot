package unitControlModule.stateFactories.actions.executableActions.steering;

import bwapiMath.Vector;

/**
 * SteeringOperation.java --- Interface for all SteeringOperations the Unit can
 * use to change various Vectors.
 * 
 * @author P H - 30.06.2017
 *
 */
public interface SteeringOperation {

	/**
	 * Function for applying the steering force to a given Vector.
	 * 
	 * @param targetVector
	 *            the Vector to which the steering force will be applied.
	 * @param intensity
	 *            the intensity with which the steering force will be applied.
	 *            Greater values will have a higher impact on the Vector
	 *            overall.
	 */
	public void applySteeringForce(Vector targetVector, Double intensity);
}
