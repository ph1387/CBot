package unitControlModule.stateFactories.updater;

import java.util.HashMap;

import javaGOAP.GoapState;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * WorldStateUpdater.java --- Superclass for updating most WorldStates.
 * 
 * @author P H - 26.02.2017
 *
 */
public abstract class WorldStateUpdaterGeneral implements Updater {

	protected PlayerUnit playerUnit;

	// Save the references to used GoapStates inside a HashMap for future
	// access.
	private HashMap<String, GoapState> mappedWorldStates = new HashMap<>();

	public WorldStateUpdaterGeneral(PlayerUnit playerUnit) {
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
		GoapState worldState = null;

		// The world state is missing and not yet added to the HashMap.
		if (!this.mappedWorldStates.containsKey(effect)) {
			GoapState missingState = null;

			// Search for the world state.
			for (GoapState state : this.playerUnit.getWorldState()) {
				if (state.effect.equals(effect)) {
					missingState = state;

					break;
				}
			}

			if (missingState != null) {
				this.mappedWorldStates.put(effect, missingState);
			}
		}

		worldState = this.mappedWorldStates.get(effect);

		// Change the value of the world state.
		if (worldState != null) {
			worldState.value = value;
		}
	}
}
