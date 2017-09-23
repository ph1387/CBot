package unitControlModule.stateFactories.actions.executableActions;

import javaGOAP.GoapState;
import unitControlModule.unitWrappers.PlayerUnitTerran_Medic;

// TODO: UML ADD
/**
 * FollowActionTerran_Medic.java --- A follow action for
 * {@link PlayerUnitTerran_Medic}s with which the Unit is able to follow another
 * one around (Mainly Terran_Marines and Terran_Firebats). It is necessary to
 * define an extra Action for this kind of behavior since the Medics would
 * otherwise simply wait around until another Unit on the map gets hurt. Then
 * and only then they would walk towards that specific Unit, which in most cases
 * is too late. With this the medics are always close to the Units that might
 * require healing.
 * 
 * @author P H - 19.09.2017
 *
 */
public class FollowActionTerran_Medic extends FollowAction {

	/**
	 * @param target
	 *            type: Unit
	 */
	public FollowActionTerran_Medic(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "isNearHealableUnit", true));
		this.addPrecondition(new GoapState(0, "isNearHealableUnit", false));
	}

	// -------------------- Functions

	@Override
	protected int defineDistanceToTarget() {
		return PlayerUnitTerran_Medic.getHealPixelDistance();
	}

}
