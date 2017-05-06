package buildingOrderModule.buildActionManagers;

import buildingOrderModule.CommandSender;
import bwapi.Race;
import core.Core;
import informationStorage.InformationStorage;
import javaGOAP.GoapUnit;

/**
 * BuildActionManagerFactory.java --- Factory for managing the different types
 * of building managers.
 * 
 * @author P H - 28.04.2017
 *
 */
public class BuildActionManagerFactory {

	// -------------------- Functions

	/**
	 * Function for creating a new BuildActionManager based on the Player's
	 * race.
	 * 
	 * @param sender
	 *            the sender which will be used for transferring the actions to
	 *            the UnitControlModule.
	 * @param informationStorage
	 *            the InformationStorage containing all important game
	 *            information.
	 * @return a GoapUnit with a specific BuildActionManager assigned.
	 */
	public static GoapUnit createManager(CommandSender sender, InformationStorage informationStorage) {
		Race race = Core.getInstance().getPlayer().getRace();
		GoapUnit goapUnit = null;

		// TODO: Possible Change: Add more variety
		if (race == Race.Terran) {
			goapUnit = new BuildActionManagerTerranDefault(sender, informationStorage);
		}

		return goapUnit;
	}
}
