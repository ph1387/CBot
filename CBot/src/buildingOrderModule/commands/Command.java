package buildingOrderModule.commands;

public interface Command {
	/**
	 * General execution function that gets called when the requirement is
	 * matched.
	 */
	public void execute();

	/**
	 * Provides the information if the requirement for executing the action is
	 * matched.
	 * 
	 * @return true or false depending if the requirement is matched.
	 */
	public boolean requirementMatched();
}
