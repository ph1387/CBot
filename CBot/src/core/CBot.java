package core;

import buildingModule.BuildingModule;
import bwapi.Color;
import bwapi.Game;
import bwapi.Mirror;
import bwapi.Unit;
import bwta.BWTA;
import cBotBWEventDistributor.CBotBWEventDistributor;
import cBotBWEventDistributor.CBotBWEventListener;
import init.Init;

class CBot implements CBotBWEventListener {
	private Mirror mirror = new Mirror();
	private Game game;

	private static CBot instance;

	private CBot() {

	}

	// -------------------- Functions

	/**
	 * Singleton function.
	 * 
	 * @return instance of the class.
	 */
	public static CBot getInstance() {
		if (instance == null) {
			instance = new CBot();
		}
		return instance;
	}

	// Set the global eventlistener and start the bot
	public void run() {
		try {
			// Initialize the global event distributor as the receiver off all game
			// events
			this.mirror.getModule().setEventListener(CBotBWEventDistributor.getInstance());

			CBotBWEventDistributor.getInstance().addListener(this);

			// Start the bot
			this.mirror.startGame();
			
			System.out.println("---RUN: success---");
		} catch(Exception e) {
			System.out.println("---RUN: failed---");
			e.printStackTrace();
		}
	}

	// -------------------- Eventlisteners

	// ------------------------------ Own CBotBWEventListener

	@Override
	public void onStart() {
		try {
			// Initialize all necessary things
			Init.init(this.mirror);

			this.game = Core.getInstance().getGame();

			System.out.println("---STARTUP: success---");
		} catch (Exception e) {
			System.out.println("---STARTUP: failed---");
			e.printStackTrace();
		}
	}

	@Override
	public void onFrame() {
		// Show Information regarding the game
		Display.showGameInformation(this.game);
		
		// Show all units
		Display.showUnits(game, this.game.self().getUnits());
	}

	@Override
	public void onUnitCreate(Unit unit) {

	}

	@Override
	public void onUnitComplete(Unit unit) {
		
	}

	@Override
	public void onUnitDestroy(Unit unit) {

	}
}