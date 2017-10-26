package unitControlModule.stateFactories.updater;

import unitControlModule.stateFactories.goals.UnitGoalStateStealth;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * GoalStateUpdaterStealth.java --- Updater for updating a
 * {@link UnitGoalStateStealth} instance.
 * 
 * @author P H - 07.10.2017
 *
 */
public class GoalStateUpdaterStealth extends GoalStateUpdaterDefault {

	public GoalStateUpdaterStealth(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	public void update(PlayerUnit playerUnit) {
		super.update(playerUnit);

		if (playerUnit.getUnit().isCloaked()) {
			// No enemy near => Decloaking.
			// Enemy unit attackable and detector near => Decloaking.
			if (playerUnit.getAttackableEnemyUnitToReactTo() == null
					|| (playerUnit.getAttackableEnemyUnitToReactTo() != null && !playerUnit.isInvulnerable())) {
				this.changeGoalStateImportance("isDecloaked", 20);
				this.changeGoalStateImportance("isCloaked", 1);
			}
		} else {
			// Enemy unit attackable and no detector near => Cloaking.
			if (playerUnit.getAttackableEnemyUnitToReactTo() != null && !PlayerUnit.isDetected(playerUnit.getUnit())) {
				this.changeGoalStateImportance("isCloaked", 20);
				this.changeGoalStateImportance("isDecloaked", 1);
			}
		}
	}
}
