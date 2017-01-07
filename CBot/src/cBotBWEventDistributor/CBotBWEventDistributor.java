package cBotBWEventDistributor;

import java.util.ArrayList;
import java.util.List;

import bwapi.DefaultBWListener;
import bwapi.Unit;

// ---------------------------------------------//
// Used to spread all events to the locations
// where they are needed.
// -> Event-distributor
// ---------------------------------------------//

public class CBotBWEventDistributor extends DefaultBWListener {

	public static CBotBWEventDistributor instance;

	private List<Object> listeners = new ArrayList<Object>();
	// Wait until the first frame passed, so that the start units and minerals
	// do not get added to the lists
	private boolean firstFrameOver = false;

	private CBotBWEventDistributor() {

	}

	// Singleton function
	public static CBotBWEventDistributor getInstance() {
		if (instance == null) {
			instance = new CBotBWEventDistributor();
		}
		return instance;
	}

	@Override
	public void onUnitComplete(Unit unit) {
		if (this.firstFrameOver) {
			this.dispatchNewUnitCompleteEvent(unit);
		}
	}

	@Override
	public void onUnitCreate(Unit unit) {
		if (this.firstFrameOver) {
			this.dispatchNewUnitCreateEvent(unit);
		}
	}

	@Override
	public void onStart() {
		this.dispatchNewStartEvent();
	}

	@Override
	public void onFrame() {
		// Unlock other events
		this.firstFrameOver = true;
		this.dispatchNewFrameEvent();
	}

	@Override
	public void onUnitDestroy(Unit unit) {
		if(this.firstFrameOver) {
			this.dispatchNewUnitDestroyEvent(unit);
		}
	}

	// -------------------- Events

	public synchronized void addListener(Object listener) {
		this.listeners.add(listener);
	}

	public synchronized void removeListener(Object listener) {
		this.listeners.remove(listener);
	}

	// ------------------------------ Unit completed
	private synchronized void dispatchNewUnitCompleteEvent(Unit unit) {
		for (Object listener : this.listeners) {
			((CBotBWEventListener) listener).onUnitComplete(unit);
		}
	}

	// ------------------------------ Unit created
	private synchronized void dispatchNewUnitCreateEvent(Unit unit) {
		for (Object listener : this.listeners) {
			((CBotBWEventListener) listener).onUnitCreate(unit);
		}
	}

	// ------------------------------ Start
	private synchronized void dispatchNewStartEvent() {
		for (Object listener : this.listeners) {
			((CBotBWEventListener) listener).onStart();
		}
	}

	// ------------------------------ Frame
	private synchronized void dispatchNewFrameEvent() {
		for (Object listener : this.listeners) {
			((CBotBWEventListener) listener).onFrame();
		}
	}

	// ------------------------------ Unit destroyed
	private synchronized void dispatchNewUnitDestroyEvent(Unit unit) {
		for (Object listener : this.listeners) {
			((CBotBWEventListener) listener).onUnitDestroy(unit);
		}
	}
}
