package unitControlModule.stateFactories.actions.executableActions;

import bwta.Chokepoint;

// TODO: UML ADD
/**
 * SmartlyMovingPerformer.java --- Interface used for Units capable of moving
 * smartly between ChokePoints towards a certain target.
 * 
 * @author P H - 13.04.2018
 *
 */
public interface SmartlyMovingPerformer {

	/**
	 * Function for retrieving the information if the performing Unit is already
	 * smartly moving and therefore does not need to generate a new List of
	 * traversable ChokePoints.
	 * 
	 * @return true if the Unit is already smartly moving and was issued to move
	 *         from ChokePoint to ChokePoint, otherwise false.
	 */
	public boolean isAlreadySmartlyMoving();

	/**
	 * Function for retrieving the last ChokePoint the smartly moving Unit has
	 * to move to.
	 * 
	 * @return the last ChokePoint the Unit has to move to or null, if the Unit
	 *         has none.
	 */
	public Chokepoint getLastChokePoint();

	/**
	 * Function for setting the last ChokePoint the Unit has to move to.
	 * 
	 * @param chokePoint
	 *            the last ChokePoint the Unit has to move to.
	 */
	public void setLastChokePoint(Chokepoint chokePoint);

	/**
	 * Function for setting the flag indicating if the Unit has already reached
	 * the last ChokePoint.
	 * 
	 * @param value
	 *            the value of the flag that is being set.
	 */
	public void setReachedLastChokePoint(boolean value);

	/**
	 * Function for retrieving the value of the flag indicating if the Unit has
	 * reached the last ChokePoint that it has to move to.
	 * 
	 * @return true if the Unit has reached the last ChokePoint, otherwise
	 *         false.
	 */
	public boolean hasReachedLastChokePoint();

	/**
	 * Function for resetting all values associated with the smartly moving
	 * between ChokePoints (I.e. flags, ChokePoints, ...).
	 */
	public void resetSmartlyMovingValues();

}
