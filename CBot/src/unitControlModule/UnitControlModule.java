package unitControlModule;

import java.util.ArrayList;
import java.util.List;

import bwapi.*;
import cBotBWEventDistributor.CBotBWEventDistributor;
import cBotBWEventDistributor.CBotBWEventListener;
import unitControlModule.scoutCommandManager.ScoutCommandManager;

public class UnitControlModule implements CBotBWEventListener {

	private static UnitControlModule instance;

	private ScoutCommandManager scoutCommandManager = null;

	private List<Unit> combatUnits = new ArrayList<Unit>();

	private UnitControlModule() {
		CBotBWEventDistributor.getInstance().addListener(this);
	}

	// -------------------- Functions

	// Singleton function
	public static UnitControlModule getInstance() {
		if (instance == null) {
			instance = new UnitControlModule();
		}
		return instance;
	}

	// -------------------- Eventlisteners

	// ------------------------------ Own CBotBWEventListener
	@Override
	public void onStart() {

	}

	@Override
	public void onFrame() {
		// If a combat unit is in the combat unit list, assign it to a scout
		// command manager and scout the map with it
		if (!this.combatUnits.isEmpty() && this.scoutCommandManager == null) {
			this.scoutCommandManager = new ScoutCommandManager(this.combatUnits.get(0));
			this.scoutCommandManager.generateBaseScoutingList();
		}

		// Run the scout manager
		if (this.scoutCommandManager != null) {
			this.scoutCommandManager.runCommands();
		}
	}

	@Override
	public void onUnitCreate(Unit unit) {

	}

	@Override
	public void onUnitComplete(Unit unit) {
		// Every time a combat unit is completed, add it to the list of combat
		// units
		if (!unit.getType().isBuilding() && !unit.getType().isWorker()) {
			this.combatUnits.add(unit);
		}
	}

	@Override
	public void onUnitDestroy(Unit unit) {
		// When a unit gets destroyed, test if it was a combat unit of the list
		if (!unit.getType().isBuilding() && !unit.getType().isWorker()) {
			this.combatUnits.remove(unit);
		}
	}
}
