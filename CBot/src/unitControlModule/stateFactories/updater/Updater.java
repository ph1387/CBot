package unitControlModule.stateFactories.updater;

import unitControlModule.unitWrappers.PlayerUnit;

public interface Updater {
	/**
	 * General update function which gets called when the implementing classes
	 * should update their corresponding values / references.
	 * 
	 * @param playerUnit the PlayerUnit whose properties may change.
	 */
	public void update(PlayerUnit playerUnit);
}
