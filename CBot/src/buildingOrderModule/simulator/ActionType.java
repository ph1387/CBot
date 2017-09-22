package buildingOrderModule.simulator;

/**
 * ActionType.java --- The interface on which the Simulator performs its
 * simulation. Each action that is considered must implement this Interface.
 * 
 * @author P H - 06.07.2017
 *
 */
public interface ActionType {

	/**
	 * Function for defining the {@link TypeWrapper} that is required for
	 * performing / performing the Action itself. I.e.:
	 * <ul>
	 * <li>Construction actions require a worker to work.</li>
	 * <li>Addon actions require the type of building that they are being
	 * constructed at.</li>
	 * <li>Research actions require the type of building that they are being
	 * researched at.</li>
	 * <li>Training actions require teh type of building that they are being
	 * trained at.</li>
	 * </ul>
	 * 
	 * @return the Type that is required for performing the Action.
	 */
	public TypeWrapper defineRequiredType();

	/**
	 *
	 * @return the Type that this Action is producing.
	 */
	public TypeWrapper defineResultType();

	/**
	 *
	 * @return the score that this Action is giving.
	 */
	public int defineScore();

	/**
	 *
	 * @return the mineral cost of this Action.
	 */
	public int defineMineralCost();

	/**
	 *
	 * @return the gas cost of this Action.
	 */
	public int defineGasCost();

	/**
	 *
	 * @return the time in frames that it takes to complete the Action.
	 */
	public int defineCompletionTime();

	/**
	 *
	 * @return the maximum number of times the ActionType may occur in a
	 *         complete simulation cycle. -1 counts as undefined and therefore
	 *         the ActionType may be added as desired.
	 */
	public int defineMaxSimulationOccurrences();

}
