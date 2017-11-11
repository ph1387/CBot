package unitControlModule.stateFactories.updater;

import unitControlModule.stateFactories.actions.AvailableActionsTerran_SiegeTank;
import unitControlModule.stateFactories.actions.executableActions.AttackMoveActionTerran_SiegeTank;
import unitControlModule.stateFactories.actions.executableActions.AttackUnitActionTerran_SiegeTank_Bombard;
import unitControlModule.stateFactories.actions.executableActions.BaseAction;
import unitControlModule.stateFactories.actions.executableActions.TerranSiegeTank_TankMode_MoveIntoExpectedSiegeRange;
import unitControlModule.stateFactories.actions.executableActions.TerranSiegeTank_TankMode_MoveIntoSiegeRange;
import unitControlModule.stateFactories.actions.executableActions.abilities.AbilityActionTerranSiegeTank_SiegeMode;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * Terran_SiegeTankActionUpdater.java --- Updater for updating an
 * {@link AvailableActionsTerran_SiegeTank} instance.
 * 
 * @author P H - 25.03.2017
 *
 */
public class ActionUpdaterTerran_SiegeTank extends ActionUpdaterDefault {

	private AbilityActionTerranSiegeTank_SiegeMode abilityActionTerranSiegeTankSiegeMode;
	private AttackUnitActionTerran_SiegeTank_Bombard abilityActionTerranSiegeTankBombard;

	private TerranSiegeTank_TankMode_MoveIntoSiegeRange terranSiegeTank_TankMode_MoveIntoSiegeRange;
	// TODO: UML ADD
	private TerranSiegeTank_TankMode_MoveIntoExpectedSiegeRange terranSiegeTank_TankMode_MoveIntoExpectedSiegeRange;

	public ActionUpdaterTerran_SiegeTank(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	public void update(PlayerUnit playerUnit) {
		super.update(playerUnit);

		// TODO: Possible Change: Only perform once.
		this.abilityActionTerranSiegeTankSiegeMode.setTarget(this.playerUnit);

		this.abilityActionTerranSiegeTankBombard.setTarget(this.playerUnit.getAttackableEnemyUnitToReactTo());
		this.terranSiegeTank_TankMode_MoveIntoSiegeRange.setTarget(this.playerUnit.getAttackableEnemyUnitToReactTo());
		this.terranSiegeTank_TankMode_MoveIntoExpectedSiegeRange
				.setTarget(this.playerUnit.getAttackableEnemyUnitToReactTo());
	}

	@Override
	protected void init() {
		super.init();

		this.abilityActionTerranSiegeTankSiegeMode = ((AbilityActionTerranSiegeTank_SiegeMode) this
				.getActionFromInstance(AbilityActionTerranSiegeTank_SiegeMode.class));
		this.abilityActionTerranSiegeTankBombard = ((AttackUnitActionTerran_SiegeTank_Bombard) this
				.getActionFromInstance(AttackUnitActionTerran_SiegeTank_Bombard.class));
		this.terranSiegeTank_TankMode_MoveIntoSiegeRange = ((TerranSiegeTank_TankMode_MoveIntoSiegeRange) this
				.getActionFromInstance(TerranSiegeTank_TankMode_MoveIntoSiegeRange.class));
		this.terranSiegeTank_TankMode_MoveIntoExpectedSiegeRange = ((TerranSiegeTank_TankMode_MoveIntoExpectedSiegeRange) this
				.getActionFromInstance(TerranSiegeTank_TankMode_MoveIntoExpectedSiegeRange.class));
	}

	// TODO: UML ADD
	@Override
	protected BaseAction initAttackMoveAction() {
		return ((AttackMoveActionTerran_SiegeTank) this.getActionFromInstance(AttackMoveActionTerran_SiegeTank.class));
	}
}
