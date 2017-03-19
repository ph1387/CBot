package core;

import bwapi.BWEventListener;
import bwapi.Game;
import bwapi.Mirror;
import bwapi.Player;
import bwapi.Position;
import bwapi.Unit;
import unitControlModule.UnitControlModule;
import unitTrackerModule.UnitTrackerModule;

/**
 * CBot.java --- The bot-class itself of which an instance gets created. This
 * class receives all events from the BWAPI-Eventlistener.
 * 
 * @author P H - 18.03.2017
 *
 */
class CBot implements BWEventListener {
	private static CBot instance;
	
	private Mirror mirror = new Mirror();
	private Game game;
	private boolean started = false;
	private boolean addedUnits = false;

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

	/**
	 * Run the bot.
	 */
	public void run() {
		try {
			this.mirror.getModule().setEventListener(this);
			this.mirror.startGame();

			System.out.println("---RUN: success---");
		} catch (Exception e) {
			System.out.println("---RUN: failed---");
			e.printStackTrace();
		}
	}

	// -------------------- Eventlisteners

	// ------------------------------ BWEventlistener
	@Override
	public void onStart() {
		try {
			if (!Init.init(this.mirror)) {
				throw new Exception();
			}

			this.game = Core.getInstance().getGame();
			this.started = true;
			
			System.out.println("---STARTUP: success---");
		} catch (Exception e) {
			System.out.println("---STARTUP: failed---");
			e.printStackTrace();
		}
	}

	@Override
	public void onFrame() {
		if(!this.addedUnits && this.started) {
			System.out.println("Assigned Units:");
			
			// Add all known Units to the UnitControl
			for (Unit unit : this.game.self().getUnits()) {
				UnitControlModule.getInstance().addToUnitControl(unit);
				System.out.println("  - " + unit.getType());
			}
			
			this.addedUnits = true;
		}
		
		if(this.started) {
			Display.showGameInformation(this.game);
			Display.showUnits(game, this.game.self().getUnits());

			UnitTrackerModule.getInstance().update();
			UnitControlModule.getInstance().update();
		}
	}

	@Override
	public void onUnitCreate(Unit unit) {

	}

	@Override
	public void onUnitComplete(Unit unit) {
		if(!unit.getType().isBuilding()) {
			UnitControlModule.getInstance().addToUnitControl(unit);
		}
	}

	@Override
	public void onUnitDestroy(Unit unit) {
		if(!unit.getType().isBuilding()) {
			UnitControlModule.getInstance().removeUnitFromUnitControl(unit);
		}
	}

	@Override
	public void onEnd(boolean arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNukeDetect(Position arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPlayerDropped(Player arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPlayerLeft(Player arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReceiveText(Player arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSaveGame(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSendText(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnitDiscover(Unit arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnitEvade(Unit arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnitHide(Unit arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnitMorph(Unit arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnitRenegade(Unit arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnitShow(Unit arg0) {
		// TODO Auto-generated method stub

	}
}