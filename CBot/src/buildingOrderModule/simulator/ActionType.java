package buildingOrderModule.simulator;

// TODO: UML ADD
/**
 * ActionType.java --- The interface on which the Simulator performs its
 * simulation. Each action that is considered must implement this Interface.
 * 
 * @author P H - 06.07.2017
 *
 */
public interface ActionType {

	/**
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
