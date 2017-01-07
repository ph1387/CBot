package init;

import buildingModule.BuildingModule;
import buildingOrderModule.BuildingOrderModule;
import bwapi.Game;
import bwapi.Mirror;
import bwta.BWTA;
import core.Core;
import unitControlModule.UnitControlModule;

public class Init {
	private static final int UNIT_FLAG = 1;
	private static final int GAME_SPEED = 0;

	// Function for initializing all important Functions in the beginning. Has
	// to be called once!
	public static boolean init(Mirror mirror) {
		boolean successful = true;

		try {
			// Get most important references to the current game and player
			Game game = mirror.getGame();

			// Instantiate the core and module(s)
			Core.getInstance().setMirror(mirror);
			BuildingModule.getInstance();
			BuildingOrderModule.getInstance();
			UnitControlModule.getInstance();

			// Use BWTA to analyze map
			BWTA.readMap();
			BWTA.analyze();

			// Change game settings
			game.enableFlag(UNIT_FLAG);
			game.setLocalSpeed(GAME_SPEED);
		} catch (Exception e) {
			System.out.println("---INIT FAILED---");
			successful = false;
		}
		return successful;
	}
}
