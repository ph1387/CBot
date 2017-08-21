package unitControlModule.stateFactories.actions.executableActions;

import bwapiMath.Point;

/**
 * RetreatUnit.java --- Interface for all Units that are being grouped in / use
 * the {@link RetreatPositionCluster}.
 * 
 * @author P H - 19.08.2017
 *
 */
public interface RetreatUnit {

	/**
	 * Function for defining the current Position of the Unit.
	 * 
	 * @return the current Position of the Unit.
	 */
	public Point defineCurrentPosition();
}
