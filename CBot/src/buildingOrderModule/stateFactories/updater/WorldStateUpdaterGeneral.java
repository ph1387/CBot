package buildingOrderModule.stateFactories.updater;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import javaGOAP.GoapState;

/**
 * WorldStateUpdaterGeneral.java --- Superclass for updating most WorldStates.
 * 
 * @author P H - 28.04.2017
 *
 */
public abstract class WorldStateUpdaterGeneral implements Updater {

	protected BuildActionManager buildActionManager;

	public WorldStateUpdaterGeneral(BuildActionManager buildActionManager) {
		this.buildActionManager = buildActionManager;
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
		for (GoapState state : this.buildActionManager.getWorldState()) {
			if (state.effect.equals(effect)) {
				state.value = value;

				break;
			}
		}
	}
}
