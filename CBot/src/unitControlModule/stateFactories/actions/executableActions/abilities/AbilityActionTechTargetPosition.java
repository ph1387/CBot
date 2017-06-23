package unitControlModule.stateFactories.actions.executableActions.abilities;

import bwapi.Position;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

// TODO: UML ADD
/**
 * AbilityActionTechTargetPosition.java --- Action for performing an ability on
 * a Position.
 * 
 * @author P H - 23.06.2017
 *
 */
public abstract class AbilityActionTechTargetPosition extends AbilityActionGeneralSuperclass {

	/**
	 * @param target
	 *            type: Position
	 */
	public AbilityActionTechTargetPosition(Object target) {
		super(target);
	}

	// -------------------- Functions

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().useTech(this.ability, (Position) this.target);
	}
}
