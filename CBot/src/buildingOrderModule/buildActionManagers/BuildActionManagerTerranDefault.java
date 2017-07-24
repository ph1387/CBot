package buildingOrderModule.buildActionManagers;

import buildingOrderModule.CommandSender;
import buildingOrderModule.stateFactories.StateFactory;
import buildingOrderModule.stateFactories.StateFactoryTerranBasic;
import informationStorage.InformationStorage;

/**
 * BuildActionManagerTerranDefault.java --- Class for controlling the building,
 * research etc. behavior of a Terran type bot.
 * 
 * @author P H - 28.04.2017
 *
 */
public class BuildActionManagerTerranDefault extends BuildActionManager {

	public BuildActionManagerTerranDefault(CommandSender sender, InformationStorage informationStorage) {
		super(sender, informationStorage);
	}

	// -------------------- Functions

	@Override
	protected StateFactory createFactory() {
		return new StateFactoryTerranBasic();
	}
	
}
