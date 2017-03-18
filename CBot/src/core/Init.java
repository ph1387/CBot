package core;

import buildingModule.BuildingModule;
import buildingOrderModule.BuildingOrderModule;
import bwapi.Game;
import bwapi.Mirror;
import bwta.BWTA;
import unitControlModule.UnitControlModule;
import unitTrackerModule.UnitTrackerModule;

/**
 * Init.java --- Class used for the initialization of the most important
 * instances.
 * 
 * @author P H - 18.03.2017
 *
 */
public class Init {
	private static final int UNIT_FLAG = 1;
	private static final int GAME_SPEED = 60;

	/**
	 * Function for initializing all important Functions in the beginning.
	 * 
	 * @param mirror
	 *            the mirror of the game.
	 * @return true or false depending if the action as successful or not.
	 */
	public static boolean init(Mirror mirror) {
		boolean successful = true;

		try {
			Game game = mirror.getGame();

			Core.getInstance().setMirror(mirror);

			// Use BWTA to analyze map
			BWTA.readMap();
			BWTA.analyze();

			// Change game settings
			game.enableFlag(UNIT_FLAG);
			game.setLocalSpeed(GAME_SPEED);
		} catch (Exception e) {
			System.out.println("---INIT FAILED---");
			e.printStackTrace();
			successful = false;
		}
		return successful;
	}
}
