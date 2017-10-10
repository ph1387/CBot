package unitControlModule.stateFactories.actions.executableActions;

import bwapi.Unit;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;
import unitControlModule.unitWrappers.PlayerUnitTerran_ScienceVessel;

// TODO: UML ADD
/**
 * FollowActionTerran_ScienceVessel.java --- A follow action for
 * {@link PlayerUnitTerran_ScienceVessel}s with which the Unit is able to follow
 * another one around. The Terran_Science_Vessel defines an order in which the
 * existing Units are checked for compatibility. The executing Unit then
 * performs this Action (= Follows the Unit) until it is in support range which
 * in this Class is defined as the support pixel range of the
 * {@link PlayerUnitTerran_ScienceVessel} Class.
 * 
 * @author P H - 23.09.2017
 *
 */
public class FollowActionTerran_ScienceVessel extends FollowAction {

	/**
	 * @param target
	 *            type: Unit
	 */
	public FollowActionTerran_ScienceVessel(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "isNearSupportableUnit", true));
		this.addEffect(new GoapState(0, "isFollowingUnit", true));
		this.addPrecondition(new GoapState(0, "isNearSupportableUnit", false));
	}

	// -------------------- Functions

	@Override
	protected int defineDistanceToTarget() {
		return PlayerUnitTerran_ScienceVessel.getSupportPixelDistance();
	}

	// TODO: UML ADD OVERRIDE
	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		PlayerUnit playerUnit = (PlayerUnit) goapUnit;

		// Renew the mapping of the followed Unit from the last iteration since
		// the target of the action may change!
		playerUnit.getInformationStorage().getScienceVesselStorage().followUnit(playerUnit.getUnit(),
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
			playerUnit.getInformationStorage().getScienceVesselStorage().unfollowUnit(playerUnit.getUnit());
		}

		super.resetSpecific();
	}

	// ------------------------------ Getter / Setter

}
