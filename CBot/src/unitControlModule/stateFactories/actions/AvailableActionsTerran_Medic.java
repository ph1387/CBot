package unitControlModule.stateFactories.actions;

import unitControlModule.stateFactories.actions.executableActions.ProtectMoveActionSteerTowardsClosestDamagedUnit;
import unitControlModule.stateFactories.actions.executableActions.RetreatActionSteerInBioUnitDirectionTerran_Medic;

/**
 * AvailableActionsTerran_Medic.java --- HashSet containing all Terran_Medic
 * Actions.
 * 
 * @author P H - 27.06.2017
 *
 */
public class AvailableActionsTerran_Medic extends AvailableActionsGeneral {

	public AvailableActionsTerran_Medic() {
		this.add(new RetreatActionSteerInBioUnitDirectionTerran_Medic(null));
		this.add(new ProtectMoveActionSteerTowardsClosestDamagedUnit(null));
	}
}
