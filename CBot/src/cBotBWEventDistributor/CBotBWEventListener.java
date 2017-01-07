package cBotBWEventDistributor;

import bwapi.*;

public interface CBotBWEventListener {
	public void onStart();
	
	public void onFrame();
	
	public void onUnitCreate(Unit unit);
	
	public void onUnitComplete(Unit unit);
	
	public void onUnitDestroy(Unit unit);
}
