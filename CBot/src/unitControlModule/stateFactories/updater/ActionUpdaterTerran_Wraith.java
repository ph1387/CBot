package unitControlModule.stateFactories.updater;

import unitControlModule.stateFactories.actions.AvailableActionsTerran_Wraith;
import unitControlModule.stateFactories.actions.executableActions.abilities.AbilityActionTerranWraith_Cloak;
import unitControlModule.stateFactories.actions.executableActions.abilities.AbilityActionTerranWraith_Decloak;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * ActionUpdaterTerran_Wraith.java --- Updater for updating an
 * {@link AvailableActionsTerran_Wraith} instance.
 * 
 * @author P H - 13.09.2017
 *
 */
public class ActionUpdaterTerran_Wraith extends ActionUpdaterDefault {

	private AbilityActionTerranWraith_Cloak abilityActionTerranWraith_Cloak;
	private AbilityActionTerranWraith_Decloak abilityActionTerranWraith_Decloak;

	public ActionUpdaterTerran_Wraith(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	public void update(PlayerUnit playerUnit) {
		super.update(playerUnit);

		this.abilityActionTerranWraith_Cloak.setTarget(playerUnit);
		this.abilityActionTerranWraith_Decloak.setTarget(playerUnit);
	}

	@Override
	protected void init() {
		super.init();

		this.abilityActionTerranWraith_Cloak = ((AbilityActionTerranWraith_Cloak) this
				.getActionFromInstance(AbilityActionTerranWraith_Cloak.class));
		this.abilityActionTerranWraith_Decloak = ((AbilityActionTerranWraith_Decloak) this
				.getActionFromInstance(AbilityActionTerranWraith_Decloak.class));
	}

}
