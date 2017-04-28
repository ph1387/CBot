package buildingOrderModule.buildActionManagers;

import buildingOrderModule.CommandSender;
import buildingOrderModule.stateFactories.StateFactory;
import buildingOrderModule.stateFactories.StateFactoryTerranBasic;

/**
 * BuildActionManagerTerranDefault.java --- Class for controlling the building,
 * research etc. behavior of a Terran type bot.
 * 
 * @author P H - 28.04.2017
 *
 */
public class BuildActionManagerTerranDefault extends BuildActionManager {

	public BuildActionManagerTerranDefault(CommandSender sender) {
		super(sender);
	}

	// -------------------- Functions

	@Override
	protected StateFactory createFactory() {
		return new StateFactoryTerranBasic();
	}
}
