package unitControlModule.stateFactories.actions.executableActions;

import bwapi.Unit;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;
import unitControlModule.unitWrappers.PlayerUnitTerran_SCV;

// TODO: UML ADD
/**
 * FollowActionTerran_SCV.java --- A follow action for
 * {@link PlayerUnitTerran_SCV}s with which the Unit is able to follow another
 * one around. The Class defines a List of UnitTypes which can be followed by
 * the SCVs while also defining the minimum distance to the target which equals
 * {@link PlayerUnitTerran_SCV#getRepairPixelDistance()}.
 * 
 * @author P H - 09.10.2017
 *
 */
public class FollowActionTerran_SCV extends FollowAction {

	/**
	 * @param target
	 *            type: Unit
	 */
	public FollowActionTerran_SCV(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "isNearRepairableUnit", true));
		this.addEffect(new GoapState(0, "isFollowingUnit", true));
		this.addPrecondition(new GoapState(0, "isNearRepairableUnit", false));
	}

	// -------------------- Functions

	@Override
	protected int defineDistanceToTarget() {
		return PlayerUnitTerran_SCV.getRepairPixelDistance();
	}

	// TODO: UML ADD OVERRIDE
	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		PlayerUnit playerUnit = (PlayerUnit) goapUnit;

		// Renew the mapping of the followed Unit from the last iteration since
		// the target of the action may change!
		playerUnit.getInformationStorage().getWorkerConfig().getUnitMapperFollow().mapUnit(playerUnit.getUnit(),
				(Unit) this.target);

		return super.performSpecificAction(goapUnit);
	}

	// TODO: UML ADD OVERRIDE
	@Override
	protected void resetSpecific() {
		// Remove any Units that this one might be following from the shared
		// storage.
		if (this.currentlyExecutingUnit != null) {
			PlayerUnit playerUnit = (PlayerUnit) this.currentlyExecutingUnit;
			playerUnit.getInformationStorage().getWorkerConfig().getUnitMapperFollow().unmapUnit(playerUnit.getUnit());
		}

		super.resetSpecific();
	}
}
