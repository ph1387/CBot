package unitControlModule.stateFactories.updater;

import javaGOAP.GoapState;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * WorldStateUpdater.java --- Superclass for updating most WorldStates.
 * 
 * @author P H - 26.02.2017
 *
 */
public abstract class GeneralWorldStateUpdater implements Updater {

	protected PlayerUnit playerUnit;

	public GeneralWorldStateUpdater(PlayerUnit playerUnit) {
		this.playerUnit = playerUnit;
	}

	// -------------------- Functions

	/**
	 * Change the world state accordingly.
	 * 
	 * @param effect
	 *            the effect which is going to be changed.
	 * @param value
	 *            the value the effect shall have.
	 */
	protected void changeWorldStateEffect(String effect, Object value) {
		for (GoapState state : this.playerUnit.getWorldState()) {
			if (state.effect.equals(effect)) {
				state.value = value;

				break;
			}
		}
	}
}