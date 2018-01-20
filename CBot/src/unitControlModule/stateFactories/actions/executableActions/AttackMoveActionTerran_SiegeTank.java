package unitControlModule.stateFactories.actions.executableActions;

import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnitTerran_SiegeTank;

/**
 * AttackMoveActionTerran_SiegeTank.java --- An attacking action with which the
 * unit can perform an attack move to the specified target TilePosition. This
 * action is made for Terran_Siege_Tanks as they are able to expect enemies. The
 * main difference to the general {@link AttackMoveAction} is the
 * {@link #isSpecificDone(IGoapUnit)} function, which in this instance triggers
 * / returns true when the associated {@link IGoapUnit} aka. the
 * {@link PlayerUnitTerran_SiegeTank} is expecting an enemy Unit to advance
 * towards it. The Unit will always prefer using this action over the default
 * superclass action due to the slightly lower costs.
 * 
 * @author P H - 11.11.2017
 *
 */
public class AttackMoveActionTerran_SiegeTank extends AttackMoveAction {

	/**
	 * @param target
	 *            type: TilePosition
	 */
	public AttackMoveActionTerran_SiegeTank(Object target) {
		super(target);
	}

	// -------------------- Functions

	@Override
	protected boolean isSpecificDone(IGoapUnit goapUnit) {
		return super.isSpecificDone(goapUnit) || ((PlayerUnitTerran_SiegeTank) goapUnit).isExpectingEnemy();
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return super.generateBaseCost(goapUnit) - 1.f;
	}
}
