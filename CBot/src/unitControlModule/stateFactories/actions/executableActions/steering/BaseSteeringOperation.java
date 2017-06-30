package unitControlModule.stateFactories.actions.executableActions.steering;

import javaGOAP.IGoapUnit;

/**
 * BaseSteeringOperation.java --- Most basic steering action. Superclass for all
 * other steering actions.
 * 
 * @author P H - 28.06.2017
 *
 */
public abstract class BaseSteeringOperation implements SteeringOperation {

	// The Unit that the SteeringOperation is being performed around.
	protected IGoapUnit goapUnit;

	public BaseSteeringOperation(IGoapUnit goapUnit) {
		this.goapUnit = goapUnit;
	}

	// -------------------- Functions
	
}
