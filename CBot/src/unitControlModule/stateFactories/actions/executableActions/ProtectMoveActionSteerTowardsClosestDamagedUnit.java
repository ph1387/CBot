package unitControlModule.stateFactories.actions.executableActions;

import javaGOAP.GoapState;

//TODO: UML MARK
@Deprecated
/**
 * ProtectMoveActionSteerTowardsClosestDamagedUnit.java --- A Action for a
 * PlayerUnit (!) with which it moves towards the closest injured Unit. This
 * Action is mainly targeted at support Units which can either heal, buff or
 * transport Units.
 * 
 * @author P H - 01.07.2017
 *
 */
public class ProtectMoveActionSteerTowardsClosestDamagedUnit extends ProtectMoveActionGeneralSuperclass {

	/**
	 * @param target type: Unit
	 */
	public ProtectMoveActionSteerTowardsClosestDamagedUnit(Object target) {
		super(target);
		
		this.addEffect(new GoapState(0, "isNearHealableUnit", true));
		this.addPrecondition(new GoapState(0, "isNearHealableUnit", false));
	}

	// -------------------- Functions
	
}
