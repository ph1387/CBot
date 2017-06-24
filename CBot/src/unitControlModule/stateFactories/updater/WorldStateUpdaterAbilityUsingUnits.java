package unitControlModule.stateFactories.updater;

import unitControlModule.stateFactories.worldStates.UnitWorldStateAbilityUsingUnits;
import unitControlModule.unitWrappers.PlayerUnit;

// TODO: UML ADD
/**
 * WorldStateUpdaterAbilityUsingUnits.java --- Updater for updating a
 * {@link UnitWorldStateAbilityUsingUnits} instance.
 * 
 * @author P H - 24.06.2017
 *
 */
public abstract class WorldStateUpdaterAbilityUsingUnits extends WorldStateUpdaterDefault {

	public WorldStateUpdaterAbilityUsingUnits(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	public void update(PlayerUnit playerUnit) {
		super.update(playerUnit);

		this.updateAbilitiyWorldState(playerUnit);
	}

	/**
	 * Function for all subclasses to implement. All of them must update their
	 * specific Set of WorldStates since every Unit implements their own
	 * abilities.
	 * 
	 * @param playerUnit
	 *            the PlayerUnit that is currently active.
	 */
	protected abstract void updateAbilitiyWorldState(PlayerUnit playerUnit);
}
