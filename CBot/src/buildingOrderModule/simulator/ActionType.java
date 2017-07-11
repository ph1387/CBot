package buildingOrderModule.simulator;

import bwapi.UnitType;

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
	 * @return the UnitType that is required for performing the Action.
	 */
	public UnitType defineRequiredUnitType();

	/**
	 *
	 * @return the UnitType that this Action is producing.
	 */
	public UnitType defineResultUnitType();

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
	 * Function for executing the Action.
	 */
	public void execute();
}
