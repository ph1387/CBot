package unitControlModule.unitWrappers;

/**
 * IPlayerUnitWrapper.java --- Interface for controllable wrapper Player Units.
 * Mainly used for accessing the {@link IPlayerUnitWrapper#destroy()} function.
 * 
 * @author P H - 25.03.2018
 *
 */
public interface IPlayerUnitWrapper {

	/**
	 * Function for updating the {@link IPlayerUnitWrapper} instance and
	 * providing a general update cycle.
	 */
	public void update();

	/**
	 * Function for reseting all actions, values and references associated with
	 * the {@link IPlayerUnitWrapper} instance. This function should only be
	 * called when the reference to this instance is discarded and therefore not
	 * being used anymore.
	 */
	public void destroy();

}
